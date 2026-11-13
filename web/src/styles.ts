export const BASE_CSS = `
  @import url('https://fonts.googleapis.com/css2?family=Lora:ital,wght@0,400;0,500;0,600;0,700;1,400&family=Noto+Naskh+Arabic:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

  :root {
    --parchment:    #f5f0e8;
    --parchment-2:  #ede7d9;
    --parchment-3:  #e3dac8;
    --warm-white:   #faf8f4;
    --ink:          #2c2416;
    --ink-2:        #4a3f2e;
    --ink-3:        #6b5e48;
    --ink-4:        #8c7d66;
    --sienna:       #8b4513;
    --sienna-l:     #b05a1c;
    --sienna-d:     #6b330e;
    --gold:         #c8942a;
    --gold-l:       #d4aa4a;
    --sage:         #5a7a5a;
    --border:       #d4c9b0;
    --border-l:     #e8e0ce;
    --shadow:       rgba(44,36,22,0.12);
    --shadow-m:     rgba(44,36,22,0.22);
    --r-sm: 6px;
    --r-md: 12px;
    --r-lg: 20px;
    --font-body: 'Source Sans 3', sans-serif;
    --font-serif: 'Lora', serif;
    --font-arabic: 'Noto Naskh Arabic', serif;
  }

  html { scroll-behavior: smooth; }
  body {
    font-family: var(--font-body);
    background: var(--parchment);
    color: var(--ink);
    min-height: 100vh;
    line-height: 1.6;
    -webkit-font-smoothing: antialiased;
  }

  a { color: inherit; text-decoration: none; }
  button { cursor: pointer; font-family: var(--font-body); border: none; background: none; }

  .page { min-height: 100vh; position: relative; overflow-x: hidden; }

  /* ── HOME ─────────────────────────────────────── */

  .home-page {
    display: flex;
    flex-direction: column;
    align-items: center;
    background: var(--warm-white);
  }

  .home-bg-deco {
    position: fixed; inset: 0; pointer-events: none; z-index: 0; overflow: hidden;
  }
  .deco-circle {
    position: absolute; border-radius: 50%; opacity: 0.06;
  }
  .c1 {
    width: 700px; height: 700px;
    background: radial-gradient(circle, var(--sienna), transparent);
    top: -200px; right: -150px;
  }
  .c2 {
    width: 500px; height: 500px;
    background: radial-gradient(circle, var(--gold), transparent);
    bottom: -100px; left: -100px;
  }
  .c3 {
    width: 300px; height: 300px;
    background: radial-gradient(circle, var(--sage), transparent);
    top: 40%; left: 10%;
  }

  .home-header {
    position: relative; z-index: 1;
    text-align: center;
    padding: 72px 24px 48px;
    width: 100%;
  }

  .logo-mark {
    width: 64px; height: 64px; margin: 0 auto 24px;
    color: var(--sienna);
    opacity: 0.8;
  }
  .logo-mark svg { width: 100%; height: 100%; }

  .title-ar {
    display: block;
    font-family: var(--font-arabic);
    font-size: clamp(2rem, 5vw, 3.2rem);
    font-weight: 700;
    color: var(--sienna);
    line-height: 1.3;
    letter-spacing: 0.01em;
  }
  .title-bs {
    display: block;
    font-family: var(--font-serif);
    font-size: clamp(1.1rem, 2.5vw, 1.5rem);
    font-weight: 400;
    color: var(--ink-3);
    margin-top: 6px;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  }
  .home-subtitle {
    margin-top: 12px;
    font-size: 1rem;
    color: var(--ink-4);
    font-weight: 300;
  }

  .home-main {
    position: relative; z-index: 1;
    width: 100%; max-width: 560px;
    padding: 0 24px 80px;
    display: flex; flex-direction: column; gap: 32px;
  }

  .nav-card {
    background: white;
    border: 1px solid var(--border-l);
    border-radius: var(--r-lg);
    padding: 32px;
    box-shadow: 0 4px 24px var(--shadow), 0 1px 4px var(--shadow);
  }
  .nav-card-title {
    font-family: var(--font-serif);
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--ink-2);
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-l);
  }

  .quick-links { }
  .quick-title {
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.12em;
    color: var(--ink-4);
    margin-bottom: 12px;
    font-weight: 600;
  }
  .quick-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }
  .quick-link {
    display: flex; flex-direction: column;
    padding: 12px 14px;
    background: white;
    border: 1px solid var(--border-l);
    border-radius: var(--r-sm);
    transition: all 0.15s;
    gap: 2px;
  }
  .quick-link:hover {
    border-color: var(--sienna);
    box-shadow: 0 2px 8px var(--shadow);
    transform: translateY(-1px);
  }
  .ql-ref {
    font-size: 0.72rem;
    color: var(--sienna);
    font-weight: 600;
    letter-spacing: 0.05em;
  }
  .ql-name {
    font-size: 0.85rem;
    color: var(--ink-2);
    font-weight: 500;
  }

  /* ── NAVIGATOR ────────────────────────────────── */

  .navigator { display: flex; flex-direction: column; gap: 14px; }

  .nav-mode-toggle {
    display: flex; gap: 4px;
    background: var(--parchment-2);
    border-radius: var(--r-sm);
    padding: 3px;
  }
  .nav-mode-toggle button {
    flex: 1; padding: 7px;
    border-radius: calc(var(--r-sm) - 2px);
    font-size: 0.82rem; font-weight: 500;
    color: var(--ink-3);
    transition: all 0.15s;
  }
  .nav-mode-toggle button.active {
    background: white;
    color: var(--ink);
    box-shadow: 0 1px 4px var(--shadow);
  }

  .nav-selects { display: flex; gap: 10px; }
  .select-group { flex: 1 1 0; min-width: 0; display: flex; flex-direction: column; gap: 5px; }
  .select-group:first-child { flex: 3 1 0; }
  .select-group:last-child { flex: 1 1 0; min-width: 72px; }
  .select-group label {
    font-size: 0.72rem; text-transform: uppercase;
    letter-spacing: 0.08em; color: var(--ink-4); font-weight: 600;
  }
  .select-group select {
    padding: 9px 10px;
    border: 1px solid var(--border);
    border-radius: var(--r-sm);
    background: white;
    color: var(--ink);
    font-family: var(--font-body);
    font-size: 0.9rem;
    outline: none;
    cursor: pointer;
    transition: border-color 0.15s;
    width: 100%;
    max-width: 100%;
    min-width: 0;
    box-sizing: border-box;
    text-overflow: ellipsis;
  }
  .select-group select:focus { border-color: var(--sienna); }

  .nav-text-input {
    position: relative;
  }
  .nav-text-input-row {
    display: flex;
    align-items: stretch;
    gap: 8px;
  }
  .colon-btn {
    flex-shrink: 0;
    padding: 0 16px;
    border: 2px solid var(--border);
    border-radius: var(--r-md);
    background: white;
    color: var(--ink);
    font-size: 1.8rem;
    font-weight: 700;
    cursor: pointer;
    transition: border-color 0.15s, background 0.15s;
    line-height: 1;
  }
  .colon-btn:active {
    background: var(--sienna);
    color: white;
    border-color: var(--sienna);
  }
  .nav-text-input input {
    width: 100%;
    padding: 12px 14px;
    border: 2px solid var(--border);
    border-radius: var(--r-md);
    background: white;
    color: var(--ink);
    font-family: var(--font-body);
    font-size: 1.5rem;
    font-weight: 600;
    text-align: center;
    outline: none;
    transition: border-color 0.15s;
    letter-spacing: 0.08em;
  }
  .nav-text-input input:focus { border-color: var(--sienna); }
  .nav-text-input input::placeholder { color: var(--ink-4); font-weight: 300; font-size: 1.1rem; }
  .input-hint {
    position: absolute; bottom: -18px; left: 50%; transform: translateX(-50%);
    font-size: 0.7rem; color: var(--ink-4); letter-spacing: 0.06em;
    white-space: nowrap;
  }

  .nav-go-btn {
    display: flex; align-items: center; justify-content: center; gap: 8px;
    padding: 13px 24px;
    background: linear-gradient(135deg, var(--sienna), var(--sienna-d));
    color: white;
    border-radius: var(--r-md);
    font-size: 0.95rem; font-weight: 600;
    letter-spacing: 0.04em;
    transition: all 0.15s;
    box-shadow: 0 3px 10px rgba(139,69,19,0.35);
    margin-top: 6px;
  }
  .nav-go-btn:hover {
    background: linear-gradient(135deg, var(--sienna-l), var(--sienna));
    box-shadow: 0 5px 16px rgba(139,69,19,0.45);
    transform: translateY(-1px);
  }
  .nav-go-btn svg { width: 16px; height: 16px; }

  /* ── TAFSIR PAGE ──────────────────────────────── */

  .tafsir-page { background: var(--parchment); }

  .tafsir-bg-deco {
    position: fixed; top: 0; left: 0; right: 0; height: 4px;
    background: linear-gradient(90deg, var(--sienna), var(--gold), var(--sienna));
    z-index: 100;
  }

  .tafsir-topbar {
    position: sticky; top: 0; z-index: 50;
    display: flex; align-items: center; justify-content: space-between;
    padding: 12px 24px;
    background: rgba(245,240,232,0.92);
    backdrop-filter: blur(12px);
    border-bottom: 1px solid var(--border);
    margin-top: 4px;
  }

  .back-home {
    display: flex; align-items: center; gap: 6px;
    font-size: 0.85rem; color: var(--ink-3); font-weight: 500;
    transition: color 0.15s;
  }
  .back-home:hover { color: var(--sienna); }
  .back-home svg { width: 16px; height: 16px; }

  .topbar-ref {
    display: flex; align-items: center; gap: 8px;
    font-size: 0.95rem;
  }
  .ref-sura { font-family: var(--font-arabic); font-size: 1.1rem; color: var(--sienna); }
  .ref-sep { color: var(--ink-4); }
  .ref-num { font-weight: 600; color: var(--ink-2); letter-spacing: 0.03em; }

  .topbar-nav { display: flex; gap: 4px; }
  .nav-arrow {
    display: flex; align-items: center; justify-content: center;
    width: 34px; height: 34px;
    border-radius: var(--r-sm);
    transition: all 0.15s;
    color: var(--ink-2);
  }
  .nav-arrow:hover { background: var(--parchment-3); color: var(--sienna); }
  .nav-arrow.disabled { opacity: 0.25; pointer-events: none; }
  .nav-arrow svg { width: 16px; height: 16px; }

  .tafsir-layout {
    display: grid;
    grid-template-columns: 280px 1fr;
    min-height: calc(100vh - 57px);
    max-width: 1280px;
    margin: 0 auto;
  }

  .tafsir-sidebar {
    border-right: 1px solid var(--border);
    background: var(--warm-white);
    position: sticky; top: 57px;
    height: calc(100vh - 57px);
    overflow-y: auto;
  }
  .sidebar-inner { padding: 24px 20px; display: flex; flex-direction: column; gap: 24px; }
  .sidebar-label {
    font-size: 0.72rem; text-transform: uppercase; letter-spacing: 0.1em;
    color: var(--ink-4); font-weight: 600;
  }

  .sidebar-sura-info {
    text-align: center;
    padding: 16px;
    background: var(--parchment-2);
    border-radius: var(--r-md);
    border: 1px solid var(--border-l);
  }
  .si-ar {
    font-family: var(--font-arabic); font-size: 1.8rem; color: var(--sienna);
    line-height: 1.4;
  }
  .si-bs { font-size: 0.9rem; color: var(--ink-2); font-weight: 600; margin-top: 4px; }
  .si-count { font-size: 0.75rem; color: var(--ink-4); margin-top: 2px; }

  .sidebar-pn { display: flex; flex-direction: column; gap: 8px; }
  .pn-btn {
    display: flex; align-items: center; gap: 10px;
    padding: 12px 14px;
    border-radius: var(--r-sm);
    border: 1px solid var(--border-l);
    background: white;
    transition: all 0.15s;
    color: var(--ink-2);
  }
  .pn-btn:hover { border-color: var(--sienna); background: var(--parchment); }
  .pn-btn svg { width: 16px; height: 16px; flex-shrink: 0; }
  .pn-btn div { display: flex; flex-direction: column; }
  .pn-label { font-size: 0.7rem; color: var(--ink-4); text-transform: uppercase; letter-spacing: 0.07em; }
  .pn-ref { font-size: 0.9rem; font-weight: 600; color: var(--sienna); }
  .pn-next { flex-direction: row-reverse; text-align: right; }

  .tafsir-main {
    padding: 40px;
    background: var(--warm-white);
  }

  .loading-state, .error-state {
    display: flex; flex-direction: column; align-items: center; justify-content: center;
    min-height: 400px; gap: 16px; color: var(--ink-4);
  }
  .loader-ring {
    width: 40px; height: 40px;
    border: 3px solid var(--border);
    border-top-color: var(--sienna);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }
  .error-icon { font-size: 2rem; }
  .error-hint { font-size: 0.8rem; font-family: monospace; }

  .tafsir-article { max-width: 760px; margin: 0 auto; }

  .article-header {
    display: flex; justify-content: space-between; align-items: flex-start;
    margin-bottom: 32px;
    padding-bottom: 20px;
    border-bottom: 2px solid var(--parchment-3);
  }
  .ah-sura-name { display: flex; flex-direction: column; gap: 3px; }
  .ah-ar { font-family: var(--font-arabic); font-size: 1.4rem; color: var(--sienna); }
  .ah-bs { font-size: 0.85rem; color: var(--ink-4); font-weight: 500; text-transform: uppercase; letter-spacing: 0.08em; }
  .ah-ref {
    font-size: 2.5rem; font-weight: 700; color: var(--parchment-3);
    font-family: var(--font-serif); letter-spacing: -0.02em;
    line-height: 1;
  }

  .arabic-block {
    margin-bottom: 28px;
    padding: 32px;
    background: linear-gradient(135deg, var(--parchment) 0%, var(--parchment-2) 100%);
    border-radius: var(--r-lg);
    border: 1px solid var(--border-l);
    text-align: center;
  }
  .arabic-text {
    font-family: var(--font-arabic);
    font-size: clamp(1.8rem, 4vw, 2.8rem);
    line-height: 2;
    color: var(--ink);
    direction: rtl;
    unicode-bidi: embed;
  }

  .translation-block {
    margin-bottom: 32px;
    padding: 24px 28px;
    background: white;
    border-radius: var(--r-md);
    border: 1px solid var(--border-l);
    border-left: 4px solid var(--gold);
  }
  .trans-label {
    font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.1em;
    color: var(--gold); font-weight: 700; margin-bottom: 10px;
  }
  .trans-text {
    font-family: var(--font-serif);
    font-size: 1.15rem; line-height: 1.75; color: var(--ink-2);
    font-style: italic;
  }

  .tafsir-block {
    margin-bottom: 40px;
  }
  .tafsir-label {
    display: flex; align-items: center; gap: 8px;
    font-size: 0.72rem; text-transform: uppercase; letter-spacing: 0.1em;
    color: var(--sienna); font-weight: 700;
    margin-bottom: 16px;
  }
  .tl-icon { font-size: 1rem; opacity: 0.7; }

  .tafsir-content {
    color: var(--ink-2);
    line-height: 1.9;
    font-size: 1.06rem;
    text-align: justify;
    hyphens: auto;
    -webkit-hyphens: auto;
  }
  .tafsir-content p {
    margin-bottom: 16px;
    text-align: justify;
  }
  .tafsir-content h2 {
    font-family: var(--font-serif);
    font-size: 1.08rem; font-weight: 600;
    color: var(--sienna-d);
    margin: 28px 0 12px;
    padding-left: 12px;
    border-left: 3px solid var(--gold-l);
    text-align: left;
  }
  .tafsir-content strong { color: var(--ink); }
  .tafsir-content em { color: var(--ink-3); }
  .tafsir-content hr {
    border: none; border-top: 1px solid var(--border);
    margin: 20px 0;
  }

  .article-bottom-nav {
    display: flex; justify-content: space-between; gap: 16px;
    margin-top: 40px; padding-top: 24px;
    border-top: 1px solid var(--border);
  }
  .abn-btn {
    display: flex; align-items: center; gap: 12px;
    padding: 14px 20px;
    background: white;
    border: 1px solid var(--border);
    border-radius: var(--r-md);
    flex: 1; max-width: 220px;
    transition: all 0.15s;
    color: var(--ink-2);
  }
  .abn-btn:hover { border-color: var(--sienna); box-shadow: 0 2px 8px var(--shadow); }
  .abn-btn svg { width: 18px; height: 18px; flex-shrink: 0; }
  .abn-btn div { display: flex; flex-direction: column; gap: 1px; }
  .abn-btn span { font-size: 0.72rem; color: var(--ink-4); text-transform: uppercase; letter-spacing: 0.06em; }
  .abn-btn strong { font-size: 0.95rem; color: var(--sienna); }
  .abn-next { flex-direction: row-reverse; text-align: right; margin-left: auto; }

  /* ── 404 ──────────────────────────────────────── */

  .notfound-page {
    display: flex; align-items: center; justify-content: center;
    background: var(--warm-white);
  }
  .nf-inner {
    text-align: center; padding: 48px 24px;
  }
  .nf-ar {
    font-family: var(--font-arabic);
    font-size: 5rem; color: var(--parchment-3);
    line-height: 1; margin-bottom: 24px;
    font-weight: 700;
  }
  .nf-inner h1 {
    font-family: var(--font-serif); font-size: 1.8rem;
    color: var(--ink); margin-bottom: 12px;
  }
  .nf-inner p { color: var(--ink-4); margin-bottom: 32px; }
  .nf-home-btn {
    display: inline-flex; align-items: center; gap: 8px;
    padding: 13px 28px;
    background: linear-gradient(135deg, var(--sienna), var(--sienna-d));
    color: white; border-radius: var(--r-md);
    font-size: 0.95rem; font-weight: 600;
    box-shadow: 0 3px 12px rgba(139,69,19,0.35);
    transition: all 0.15s;
  }
  .nf-home-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(139,69,19,0.4);
  }
  .nf-home-btn svg { width: 18px; height: 18px; }

  /* ── RESPONSIVE ───────────────────────────────── */

  @media (max-width: 900px) {
    .tafsir-layout {
      grid-template-columns: 1fr;
    }
    .tafsir-sidebar {
      position: static;
      height: auto;
      border-right: none;
      border-bottom: 1px solid var(--border);
    }
    .sidebar-inner { padding: 20px 16px; }
    .tafsir-main { padding: 24px 16px; }
  }

  @media (max-width: 600px) {
    .home-header { padding: 48px 16px 32px; }
    .home-main { padding: 0 16px 60px; }
    .nav-card { padding: 20px 16px; }
    .quick-grid { grid-template-columns: 1fr 1fr; }
    .tafsir-topbar { padding: 10px 16px; }
    .ref-sura { display: none; }
    .arabic-block { padding: 20px 16px; }
    .translation-block { padding: 16px 20px; }
    .article-bottom-nav { flex-direction: column; }
    .abn-btn { max-width: 100%; }
    .abn-next { flex-direction: row; text-align: left; margin-left: 0; }
  }
`;
