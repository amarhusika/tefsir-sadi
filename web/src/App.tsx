import { useState, useEffect } from "react";
import { BrowserRouter, Routes, Route, useNavigate, useParams, Link } from "react-router-dom";
import { SURA_AYAH_COUNT, SURA_NAMES } from "./constants";
import AyahNavigator from "./AyahNavigator";
import { BASE_CSS } from "./styles";

// ─── Types ─────────────────────────────────────────────────────────────────────

interface AjetData {
  arapski: string;
  prevod: string;
  tefsir_ar: string;
  tefsir_bs: string;
  vidi?: string[];
  grupa?: [number, number];
}

interface SuraIndex {
  sure: Record<string, { prevedeno: number; ukupno: number }>;
}

// ─── Helpers ───────────────────────────────────────────────────────────────────

const esc = (s: string) =>
  s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");

/** Bosanski tekst: prazni red = novi pasus, {…} = citat ajeta, [[…]] = napomena prevodioca */
function renderBs(text: string): string {
  return text
    .split(/\n\s*\n/)
    .map(p => p.trim())
    .filter(Boolean)
    .map(p => {
      // citat koji se nastavlja iz prethodnog / u sljedeći ajet
      const iO = p.indexOf("{"), iC = p.indexOf("}");
      if (iC !== -1 && (iO === -1 || iC < iO)) p = "{" + p;
      if (p.lastIndexOf("{") > p.lastIndexOf("}")) p = p + "}";
      let h = esc(p);
      h = h.replace(/\{([^{}]+)\}/g, (_, q) => `<span class="ayah-q">${q.trim()}</span>`);
      h = h.replace(/\[\[([^\]]+)\]\]/g, (_, n) => `<span class="tr-note">[${n.trim()}]</span>`);
      return `<p>${h}</p>`;
    })
    .join("");
}

/** Arapski original: {…} = citat ajeta */
function renderAr(text: string): string {
  const h = esc(text).replace(/\{([^{}]+)\}/g, (_, q) => `<span class="ayah-q">﴿${q.trim()}﴾</span>`);
  return `<p>${h}</p>`;
}

/** Bosanski tekst tefsira kao običan tekst za kopiranje */
function plainBs(text: string): string {
  return text
    .split(/\n\s*\n/)
    .map(p => p.trim())
    .filter(Boolean)
    .map(p => {
      const iO = p.indexOf("{"), iC = p.indexOf("}");
      if (iC !== -1 && (iO === -1 || iC < iO)) p = "{" + p;
      if (p.lastIndexOf("{") > p.lastIndexOf("}")) p = p + "}";
      return p
        .replace(/\{([^{}]+)\}/g, (_, q) => `„${q.trim()}“`)
        .replace(/\[\[([^\]]+)\]\]/g, (_, n) => `[${n.trim()}]`);
    })
    .join("\n\n");
}

async function copyText(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch {
    try {
      const ta = document.createElement("textarea");
      ta.value = text;
      ta.style.position = "fixed";
      ta.style.opacity = "0";
      document.body.appendChild(ta);
      ta.select();
      const ok = document.execCommand("copy");
      document.body.removeChild(ta);
      return ok;
    } catch { return false; }
  }
}

const LAST_KEY = "sadi_last_ref";
function getLastRef(): string | null {
  try {
    const v = localStorage.getItem(LAST_KEY);
    return v && /^\d+:\d+$/.test(v) ? v : null;
  } catch { return null; }
}
function setLastRef(ref: string) {
  try { localStorage.setItem(LAST_KEY, ref); } catch {}
}

let indexCache: SuraIndex | null = null;
function useSuraIndex() {
  const [idx, setIdx] = useState<SuraIndex | null>(indexCache);
  useEffect(() => {
    if (indexCache) return;
    fetch("/data/index.json")
      .then(r => r.json())
      .then(j => { indexCache = j; setIdx(j); })
      .catch(() => {});
  }, []);
  return idx;
}

// ─── Home Page ─────────────────────────────────────────────────────────────────

function HomePage() {
  const navigate = useNavigate();
  const [lastRef] = useState<string | null>(() => getLastRef());


  return (
    <div className="page home-page">
      <div className="home-bg-deco" aria-hidden="true">
        <div className="deco-circle c1" />
        <div className="deco-circle c2" />
        <div className="deco-circle c3" />
      </div>

      <header className="home-header">
        <div className="logo-mark">
          <svg viewBox="0 0 60 60" fill="none">
            <circle cx="30" cy="30" r="28" stroke="currentColor" strokeWidth="1.5" opacity="0.3"/>
            <path d="M15 30 Q22 18 30 30 Q38 42 45 30" stroke="currentColor" strokeWidth="2" fill="none" strokeLinecap="round"/>
            <circle cx="30" cy="30" r="3" fill="currentColor"/>
          </svg>
        </div>
        <h1 className="home-title">
          <span className="title-ar">تفسير السعدي</span>
          <span className="title-bs">Tefsir es-Sa'di</span>
        </h1>
        <p className="home-subtitle">
          Tejsirul-Kerimir-Rahman fi tefsiri kelamil-Mennan — šejh Abdurrahman b. Nasir es-Sa'di
        </p>
      </header>

      <main className="home-main">
        {lastRef && (() => {
          const [ls, la] = lastRef.split(":").map(Number);
          return (
            <Link to={`/${lastRef}`} className="continue-card">
              <div>
                <span className="cc-label">Nastavi čitati</span>
                <span className="cc-ref">{SURA_NAMES[ls]?.bs} · {ls}:{la}</span>
              </div>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </Link>
          );
        })()}

        <div className="nav-card">
          <h2 className="nav-card-title">Odaberi ajet</h2>
          <AyahNavigator onNavigate={(s, a) => navigate(`/${s}:${a}`)} />
        </div>


        <div className="about-card">
          <div className="quick-title">O tefsiru</div>
          <p>
            Tefsir šejha Abdurrahmana b. Nasira es-Sa'dija (u. 1376. h.) jedan je od najjasnijih i
            najkorisnijih tefsira. Ajete objašnjava jednostavnim jezikom, ističući njihova značenja,
            pouke i propise, uz čvrsto oslanjanje na razumijevanje ehli-sunneta.
          </p>
          <a className="app-link" href="https://github.com/amarhusika/tefsir-sadi"
             target="_blank" rel="noopener noreferrer">
            <img src="/app-icon.png" alt="" aria-hidden="true" />
            <span>Mobilna aplikacija</span>
          </a>
        </div>
      </main>
    </div>
  );
}

// ─── Tafsir Page ───────────────────────────────────────────────────────────────

function TafsirPage() {
  const { ref } = useParams<{ ref: string }>();
  const navigate = useNavigate();
  const [data, setData] = useState<(AjetData & { sura_naziv: string; sura_naziv_ar: string }) | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showAr, setShowAr] = useState<boolean>(() => {
    try { return localStorage.getItem("sadi_show_ar") === "1"; } catch { return false; }
  });

  const match = ref?.match(/^(\d+):(\d+)$/);
  const sura = match ? parseInt(match[1]) : null;
  const ayah = match ? parseInt(match[2]) : null;

  const isValid = sura && ayah &&
    sura >= 1 && sura <= 114 &&
    ayah >= 1 && ayah <= (SURA_AYAH_COUNT[sura] || 0);

  const hasPrev = isValid && (ayah! > 1 || sura! > 1);
  const hasNext = isValid && (ayah! < SURA_AYAH_COUNT[sura!] || sura! < 114);

  const getPrev = () => {
    if (!sura || !ayah) return null;
    if (ayah > 1) return `${sura}:${ayah - 1}`;
    if (sura > 1) return `${sura - 1}:${SURA_AYAH_COUNT[sura - 1]}`;
    return null;
  };

  const getNext = () => {
    if (!sura || !ayah) return null;
    if (ayah < SURA_AYAH_COUNT[sura]) return `${sura}:${ayah + 1}`;
    if (sura < 114) return `${sura + 1}:1`;
    return null;
  };

  const [copied, setCopied] = useState<"" | "ok" | "err">("");

  const handleCopy = async () => {
    if (!data) return;
    const parts = [
      `${SURA_NAMES[sura!]?.bs} (${sura}:${ayah})`,
      `Prijevod značenja:\n${data.prevod}`,
    ];
    if (data.tefsir_bs) {
      const grp = data.grupa ? ` (zajednički tefsir ajeta ${sura}:${data.grupa[0]}–${data.grupa[1]})` : "";
      parts.push(`Tefsir es-Sa'di${grp}:\n${plainBs(data.tefsir_bs)}`);
    } else if (!data.tefsir_ar) {
      parts.push(`Tefsir es-Sa'di: tumačenje ovog ajeta obuhvaćeno je tefsirom ajeta ${(data.vidi || []).join(" i ")}.`);
    }
    parts.push(`${window.location.origin}/${sura}:${ayah}`);
    const ok = await copyText(parts.join("\n\n"));
    setCopied(ok ? "ok" : "err");
    setTimeout(() => setCopied(""), 2000);
  };

  const toggleAr = () => {
    setShowAr(v => {
      try { localStorage.setItem("sadi_show_ar", v ? "0" : "1"); } catch {}
      return !v;
    });
  };

  useEffect(() => {
    if (!isValid) { setLoading(false); return; }
    setLoading(true);
    setError(null);
    setData(null);

    const suraStr = String(sura!).padStart(3, "0");
    fetch(`/data/sura_${suraStr}.json`)
      .then(r => {
        if (!r.ok) throw new Error("Fajl nije pronađen");
        return r.json();
      })
      .then(json => {
        const key = `${sura}:${ayah}`;
        const ajetData = json.ajeti?.[key];
        if (!ajetData) throw new Error(`Ajet ${key} nije u bazi`);
        setLastRef(key);
        setData({ ...ajetData, sura_naziv: json.naziv_bos, sura_naziv_ar: json.naziv_ar });
        setLoading(false);
        window.scrollTo({ top: 0 });
      })
      .catch(e => {
        setError(e.message);
        setLoading(false);
      });
  }, [ref]);

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      const t = e.target as HTMLElement;
      if (t && (t.tagName === "INPUT" || t.tagName === "SELECT")) return;
      if (e.key === "ArrowLeft" && hasPrev) navigate(`/${getPrev()}`);
      if (e.key === "ArrowRight" && hasNext) navigate(`/${getNext()}`);
    };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, [sura, ayah]);

  useEffect(() => {
    if (isValid) document.title = `${sura}:${ayah} ${SURA_NAMES[sura!]?.bs} — Tefsir es-Sa'di`;
  }, [sura, ayah]);

  if (!isValid) {
    return <NotFoundPage />;
  }

  const prev = getPrev();
  const next = getNext();

  const arrowL = <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>;
  const arrowR = <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>;

  const hasBs = !!data?.tefsir_bs;
  const hasAr = !!data?.tefsir_ar;

  return (
    <div className="page tafsir-page">
      <div className="tafsir-bg-deco" aria-hidden="true">
        <div className="deco-band" />
      </div>

      <nav className="tafsir-topbar">
        <Link to="/" className="back-home">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M3 12l9-9 9 9M5 10v10h5v-6h4v6h5V10"/>
          </svg>
          <span>Početna</span>
        </Link>

        <div className="topbar-ref">
          <span className="ref-sura">{SURA_NAMES[sura!]?.ar}</span>
          <span className="ref-sep">·</span>
          <span className="ref-num">{sura}:{ayah}</span>
        </div>

        <div className="topbar-nav">
          {prev ? <Link to={`/${prev}`} className="nav-arrow" title="Prethodni ajet (←)">{arrowL}</Link>
                : <span className="nav-arrow disabled">{arrowL}</span>}
          {next ? <Link to={`/${next}`} className="nav-arrow" title="Sljedeći ajet (→)">{arrowR}</Link>
                : <span className="nav-arrow disabled">{arrowR}</span>}
        </div>
      </nav>

      <div className="tafsir-layout">
        <aside className="tafsir-sidebar">
          <div className="sidebar-inner">
            <p className="sidebar-label">Idi na ajet</p>
            <AyahNavigator
              key={ref}
              onNavigate={(s, a) => navigate(`/${s}:${a}`)}
              initialSura={sura!}
              initialAyah={ayah!}
            />

            <div className="sidebar-sura-info">
              <div className="si-ar">{SURA_NAMES[sura!]?.ar}</div>
              <div className="si-bs">{SURA_NAMES[sura!]?.bs}</div>
              <div className="si-count">{SURA_AYAH_COUNT[sura!]} ajeta</div>
            </div>

            <div className="sidebar-pn">
              {prev ? (
                <Link to={`/${prev}`} className="pn-btn pn-prev">
                  {arrowL}
                  <div><span className="pn-label">Prethodni</span><span className="pn-ref">{prev}</span></div>
                </Link>
              ) : null}
              {next ? (
                <Link to={`/${next}`} className="pn-btn pn-next">
                  <div><span className="pn-label">Sljedeći</span><span className="pn-ref">{next}</span></div>
                  {arrowR}
                </Link>
              ) : null}
            </div>
          </div>
        </aside>

        <main className="tafsir-main">
          {loading && (
            <div className="loading-state">
              <div className="loader-ring" />
              <p>Učitavanje tefsira...</p>
            </div>
          )}

          {error && !loading && (
            <div className="error-state">
              <div className="error-icon">⚠</div>
              <p>{error}</p>
            </div>
          )}

          {data && !loading && (
            <article className="tafsir-article">
              <header className="article-header">
                <div className="ah-sura-name">
                  <span className="ah-ar">{data.sura_naziv_ar}</span>
                  <span className="ah-bs">{data.sura_naziv}</span>
                </div>
                <div className="ah-ref">{sura}:{ayah}</div>
              </header>

              <div className="arabic-block">
                <div className="arabic-text">{data.arapski}</div>
              </div>

              <div className="translation-block">
                <div className="trans-label">Prijevod značenja</div>
                <p className="trans-text">{data.prevod}</p>
              </div>

              <div className="tafsir-block">
                <div className="tafsir-label-row">
                  <div className="tafsir-label">
                    <span className="tl-icon">◈</span>
                    Tefsir es-Sa'di
                  </div>
                  <div className="tafsir-actions">
                    <button className={`lang-toggle copy-btn ${copied}`} onClick={handleCopy}
                            title="Kopiraj prijevod značenja i tefsir na bosanskom">
                      {copied === "ok" ? "Kopirano ✓" : copied === "err" ? "Greška pri kopiranju" : "Kopiraj"}
                    </button>
                  </div>
                </div>

                {data.grupa && (
                  <div className="group-note">
                    Zajednički tefsir ajeta {sura}:{data.grupa[0]}–{data.grupa[1]}
                  </div>
                )}

                {!hasAr && (
                  <div className="covered-note">
                    Tumačenje ovog ajeta obuhvaćeno je tefsirom susjednih ajeta
                    {data.vidi && data.vidi.length > 0 && (
                      <> — pogledaj{" "}
                        {data.vidi.map((v, i) => (
                          <span key={v}>{i > 0 && " i "}<Link to={`/${v}`}>{v}</Link></span>
                        ))}
                      </>
                    )}.
                  </div>
                )}

                {hasBs && (
                  <div className="tafsir-content" dangerouslySetInnerHTML={{ __html: renderBs(data.tefsir_bs) }} />
                )}

                {hasAr && !hasBs && (
                  <div className="pending-note">
                    Bosanski prijevod tefsira za ovaj ajet je u pripremi. Do tada je prikazan arapski original.
                  </div>
                )}

                {hasAr && hasBs && (
                  <div className="ar-toggle-row">
                    <button className="lang-toggle" onClick={toggleAr}>
                      {showAr ? "Sakrij arapski original" : "Prikaži arapski original"}
                    </button>
                  </div>
                )}

                {hasAr && (!hasBs || showAr) && (
                  <div className="tafsir-content-ar" dir="rtl" lang="ar"
                       dangerouslySetInnerHTML={{ __html: renderAr(data.tefsir_ar) }} />
                )}
              </div>

              <div className="article-bottom-nav">
                {prev ? (
                  <Link to={`/${prev}`} className="abn-btn abn-prev">
                    {arrowL}
                    <div><span>Prethodni ajet</span><strong>{prev}</strong></div>
                  </Link>
                ) : <div />}
                {next ? (
                  <Link to={`/${next}`} className="abn-btn abn-next">
                    <div><span>Sljedeći ajet</span><strong>{next}</strong></div>
                    {arrowR}
                  </Link>
                ) : <div />}
              </div>
            </article>
          )}
        </main>
      </div>
    </div>
  );
}

// ─── 404 Page ──────────────────────────────────────────────────────────────────

function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <div className="page notfound-page">
      <div className="nf-inner">
        <div className="nf-ar">٤٠٤</div>
        <h1>Stranica nije pronađena</h1>
        <p>Tražena stranica ne postoji ili je adresa pogrešna.</p>
        <button className="nf-home-btn" onClick={() => navigate("/")}>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M3 12l9-9 9 9M5 10v10h5v-6h4v6h5V10"/>
          </svg>
          Vrati me na početnu
        </button>
      </div>
    </div>
  );
}

// ─── App ───────────────────────────────────────────────────────────────────────

export default function App() {
  return (
    <BrowserRouter>
      <style>{BASE_CSS + EXTRA_CSS}</style>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/:ref" element={<TafsirPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}

// ─── Dodatni stilovi za Sa'dijev tefsir ────────────────────────────────────────

const EXTRA_CSS = `
  .tafsir-label-row {
    display: flex; align-items: center; justify-content: space-between;
    gap: 12px; flex-wrap: wrap; margin-bottom: 16px;
  }
  .tafsir-label-row .tafsir-label { margin-bottom: 0; }
  .lang-toggle {
    font-size: 0.78rem; font-weight: 600; color: var(--sienna);
    padding: 6px 12px; border: 1px solid var(--border); border-radius: 999px;
    background: white; transition: all 0.15s;
  }
  .lang-toggle:hover { border-color: var(--sienna); background: var(--parchment); }
  .tafsir-actions { display: flex; gap: 8px; flex-wrap: wrap; }
  .ar-toggle-row { display: flex; justify-content: flex-end; margin: 22px 0 10px; }
  .copy-btn.ok { color: var(--sage); border-color: var(--sage); }
  .copy-btn.err { color: #a33; border-color: #a33; }

  .continue-card {
    display: flex; align-items: center; justify-content: space-between; gap: 12px;
    padding: 18px 24px;
    background: linear-gradient(135deg, var(--sienna), var(--sienna-d));
    color: white; border-radius: var(--r-lg);
    box-shadow: 0 4px 16px rgba(139,69,19,0.3);
    transition: transform 0.15s, box-shadow 0.15s;
  }
  .continue-card:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(139,69,19,0.4); }
  .continue-card div { display: flex; flex-direction: column; gap: 2px; }
  .cc-label { font-size: 0.72rem; text-transform: uppercase; letter-spacing: 0.1em; opacity: 0.85; font-weight: 600; }
  .cc-ref { font-family: var(--font-serif); font-size: 1.15rem; font-weight: 600; }
  .continue-card svg { width: 22px; height: 22px; flex-shrink: 0; }

  .tafsir-content .ayah-q {
    font-weight: 600; color: var(--sienna-d);
  }
  .tafsir-content .ayah-q::before { content: "„"; color: var(--gold); }
  .tafsir-content .ayah-q::after  { content: "“"; color: var(--gold); }
  .tafsir-content .tr-note { color: var(--ink-4); font-size: 0.92em; }

  .tafsir-content-ar {
    margin-top: 20px;
    padding: 24px 26px;
    background: var(--parchment);
    border: 1px solid var(--border-l);
    border-radius: var(--r-md);
    font-family: var(--font-arabic);
    font-size: 1.25rem;
    line-height: 2.1;
    color: var(--ink-2);
    text-align: justify;
  }
  .tafsir-content-ar .ayah-q { color: var(--sage); font-weight: 600; }

  .pending-note, .covered-note {
    padding: 14px 18px;
    border-radius: var(--r-sm);
    font-size: 0.92rem;
    color: var(--ink-3);
    background: var(--parchment-2);
    border-left: 3px solid var(--gold);
  }
  .group-note {
    display: inline-block; margin-bottom: 14px;
    font-size: 0.78rem; font-weight: 600; color: var(--sage);
    padding: 4px 12px; border-radius: 999px;
    background: rgba(90,122,90,0.1); border: 1px solid rgba(90,122,90,0.25);
  }
  .covered-note a { color: var(--sienna); font-weight: 600; text-decoration: underline; }

  .progress-card, .about-card {
    background: white;
    border: 1px solid var(--border-l);
    border-radius: var(--r-lg);
    padding: 24px 28px;
    box-shadow: 0 2px 12px var(--shadow);
  }
  .progress-bar {
    height: 8px; background: var(--parchment-2); border-radius: 999px; overflow: hidden;
  }
  .progress-bar > div {
    height: 100%; background: linear-gradient(90deg, var(--sienna), var(--gold));
  }
  .progress-meta { font-size: 0.8rem; color: var(--ink-4); margin-top: 6px; }
  .about-card p { color: var(--ink-3); font-size: 0.95rem; line-height: 1.7; }
  .app-link {
    display: inline-flex; align-items: center; gap: 10px;
    margin-top: 16px; padding: 10px 18px;
    border: 1px solid var(--border); border-radius: 999px;
    color: var(--ink-2); font-weight: 600; font-size: 0.92rem;
    background: var(--warm-white); transition: all 0.15s;
  }
  .app-link:hover { border-color: var(--sienna); color: var(--sienna); background: var(--parchment); }
  .app-link img { width: 26px; height: 26px; border-radius: 6px; flex-shrink: 0; }
  .home-old-link { text-align: center; font-size: 0.8rem; color: var(--ink-4); }
`;
