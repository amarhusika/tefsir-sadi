import { useState, useRef } from "react";
import { SURA_AYAH_COUNT, SURA_NAMES } from "./constants";

export default function AyahNavigator({ onNavigate, initialSura, initialAyah }: {
  onNavigate: (sura: number, ayah: number) => void;
  initialSura?: number;
  initialAyah?: number;
}) {
  const [input, setInput] = useState(
    initialSura && initialAyah ? `${initialSura}:${initialAyah}` : ""
  );
  const [sura, setSura] = useState<number>(initialSura || 1);
  const [ayah, setAyah] = useState<number>(initialAyah || 1);
  const [mode, setMode] = useState<"dropdowns" | "text">("dropdowns");
  const [error, setError] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);

  const maxAyah = SURA_AYAH_COUNT[sura] || 1;

  const handleGo = () => {
    if (mode === "text") {
      const match = input.trim().match(/^(\d+)\s*[:\s]\s*(\d+)$/);
      if (!match) { setError("Pogrešan unos. Primjer: 2:255"); return; }
      const s = parseInt(match[1]);
      const a = parseInt(match[2]);
      if (s < 1 || s > 114) { setError("Pogrešan unos: sura mora biti 1–114."); return; }
      const max = SURA_AYAH_COUNT[s];
      if (a < 1 || a > max) { setError(`Pogrešan unos: sura ${s} ima ajete 1–${max}.`); return; }
      setError("");
      onNavigate(s, a);
      return;
    }
    onNavigate(sura, ayah);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleGo();
  };

  return (
    <div className="navigator">
      <div className="nav-mode-toggle">
        <button
          className={mode === "dropdowns" ? "active" : ""}
          onClick={() => setMode("dropdowns")}
        >Odaberi</button>
        <button
          className={mode === "text" ? "active" : ""}
          onClick={() => { setMode("text"); setTimeout(() => inputRef.current?.focus(), 50); }}
        >Ukucaj</button>
      </div>

      {mode === "dropdowns" ? (
        <div className="nav-selects">
          <div className="select-group">
            <label>Sura</label>
            <select
              value={sura}
              onChange={e => {
                const s = parseInt(e.target.value);
                setSura(s);
                setAyah(Math.min(ayah, SURA_AYAH_COUNT[s] || 1));
              }}
            >
              {Array.from({length: 114}, (_, i) => i + 1).map(n => (
                <option key={n} value={n}>
                  {n}. {SURA_NAMES[n]?.bs || ""} ({SURA_NAMES[n]?.ar || ""})
                </option>
              ))}
            </select>
          </div>
          <div className="select-group">
            <label>Ajet</label>
            <select value={ayah} onChange={e => setAyah(parseInt(e.target.value))}>
              {Array.from({length: maxAyah}, (_, i) => i + 1).map(n => (
                <option key={n} value={n}>{n}</option>
              ))}
            </select>
          </div>
        </div>
      ) : (
        <div className="nav-text-input">
          <div className="nav-text-input-row">
            <input
              ref={inputRef}
              type="text"
              inputMode="numeric"
              pattern="[0-9:]*"
              placeholder="npr. 2:255"
              value={input}
              onChange={e => { setInput(e.target.value.replace(/[^0-9:]/g, "")); setError(""); }}
              onKeyDown={handleKeyDown}
            />
            <button
              className="colon-btn"
              type="button"
              onClick={() => {
                if (!input.includes(":")) {
                  setInput(prev => prev + ":");
                  inputRef.current?.focus();
                }
              }}
            >:</button>
          </div>
          {error
            ? <span className="input-error" role="alert">{error}</span>
            : <span className="input-hint">Sura:Ajet</span>}
        </div>
      )}

      <button className="nav-go-btn" onClick={handleGo}>
        <span>Otvori</span>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
          <path d="M5 12h14M12 5l7 7-7 7"/>
        </svg>
      </button>
    </div>
  );
}

