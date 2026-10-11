package com.example.ai

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object ApkHtmlExporter {

    /**
     * Converts a synthesized APK project into a standalone single-file HTML5 Web App / PWA
     */
    fun exportHtmlWebApp(
        context: Context,
        result: ApkBuildResult,
        iconData: AppIconData
    ): File? {
        return try {
            val sanitizedName = result.appName.replace("[^a-zA-Z0-9_-]".toRegex(), "_").lowercase(Locale.ROOT)
            val outputDir = File(context.filesDir, "html_builds")
            if (!outputDir.exists()) outputDir.mkdirs()

            val htmlFile = File(outputDir, "${sanitizedName}_webapp.html")
            val htmlContent = generateSingleFileHtml(result, iconData)

            FileOutputStream(htmlFile).use { out ->
                out.write(htmlContent.toByteArray(Charsets.UTF_8))
            }

            // Also copy to public Downloads directory
            try {
                val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!publicDownloads.exists()) publicDownloads.mkdirs()
                val destFile = File(publicDownloads, "${sanitizedName}_webapp.html")
                htmlFile.copyTo(destFile, overwrite = true)
            } catch (e: Exception) {
                // Scoped storage fallback
                val extDownloads = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                if (extDownloads != null) {
                    val destFile = File(extDownloads, "${sanitizedName}_webapp.html")
                    htmlFile.copyTo(destFile, overwrite = true)
                }
            }

            htmlFile
        } catch (e: Exception) {
            null
        }
    }

    private fun generateSingleFileHtml(result: ApkBuildResult, icon: AppIconData): String {
        val appName = result.appName
        val archetypeTitle = result.archetype.title
        val glyph = icon.glyph
        val bgStart = icon.bgStartHex
        val bgEnd = icon.bgEndHex

        return """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>$appName — Athena Generated Web App</title>
  <style>
    :root {
      --bg: #070B12;
      --card: #0F1726;
      --card-stroke: #1E293B;
      --cyan: #00E5FF;
      --green: #06D6A0;
      --gold: #FFD166;
      --text: #F1F5F9;
      --muted: #94A3B8;
      --grad-start: $bgStart;
      --grad-end: $bgEnd;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
    body { background: var(--bg); color: var(--text); min-height: 100vh; display: flex; flex-direction: column; align-items: center; padding: 16px; }
    .container { width: 100%; max-width: 480px; display: flex; flex-direction: column; gap: 16px; }
    
    /* Header Card */
    .header-card {
      background: var(--card);
      border: 1px solid var(--card-stroke);
      border-radius: 16px;
      padding: 16px;
      display: flex;
      align-items: center;
      gap: 14px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.4);
    }
    .app-icon {
      width: 54px;
      height: 54px;
      border-radius: 14px;
      background: linear-gradient(135deg, var(--grad-start), var(--grad-end));
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      box-shadow: 0 4px 12px rgba(0,229,255,0.25);
      flex-shrink: 0;
    }
    .header-info h1 { font-size: 18px; font-weight: 700; color: #fff; margin-bottom: 2px; }
    .header-info p { font-size: 11px; color: var(--muted); }
    .badge {
      display: inline-block;
      margin-top: 4px;
      font-size: 9px;
      font-weight: 700;
      color: var(--cyan);
      background: rgba(0,229,255,0.12);
      border: 1px solid rgba(0,229,255,0.3);
      padding: 2px 8px;
      border-radius: 999px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    /* Screen Card */
    .card {
      background: var(--card);
      border: 1px solid var(--card-stroke);
      border-radius: 16px;
      padding: 16px;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .card-title { font-size: 13px; font-weight: 700; color: var(--cyan); letter-spacing: 0.5px; }

    /* Canvas Screen */
    .canvas-box {
      width: 100%;
      height: 180px;
      background: #04070D;
      border: 1px solid var(--card-stroke);
      border-radius: 12px;
      position: relative;
      overflow: hidden;
    }
    canvas { width: 100%; height: 100%; display: block; }

    /* Controls */
    .control-row { display: flex; justify-content: space-between; align-items: center; font-size: 12px; }
    .slider-container { display: flex; flex-direction: column; gap: 4px; }
    input[type=range] {
      width: 100%;
      accent-color: var(--cyan);
      cursor: pointer;
    }

    /* Buttons */
    .btn {
      width: 100%;
      padding: 12px;
      border: none;
      border-radius: 10px;
      font-size: 13px;
      font-weight: 700;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: all 0.2s ease;
    }
    .btn-primary {
      background: var(--cyan);
      color: #03141E;
      box-shadow: 0 4px 14px rgba(0,229,255,0.3);
    }
    .btn-primary:active { transform: scale(0.98); opacity: 0.9; }
    .btn-secondary {
      background: rgba(255,255,255,0.06);
      color: var(--text);
      border: 1px solid var(--card-stroke);
    }

    /* Log Box */
    .log-box {
      background: #04070D;
      border: 1px solid var(--card-stroke);
      border-radius: 10px;
      padding: 10px;
      font-family: monospace;
      font-size: 11px;
      color: var(--green);
      height: 100px;
      overflow-y: auto;
      display: flex;
      flex-direction: column-reverse;
      gap: 4px;
    }
    .log-entry { opacity: 0.9; line-height: 1.4; }

    footer {
      text-align: center;
      font-size: 11px;
      color: var(--muted);
      margin-top: 12px;
      padding-bottom: 24px;
    }
  </style>
</head>
<body>

  <div class="container">
    <!-- Header -->
    <div class="header-card">
      <div class="app-icon">$glyph</div>
      <div class="header-info">
        <h1>$appName</h1>
        <p>$archetypeTitle • Generated by Athena APK Builder</p>
        <span class="badge">Standalone Web App</span>
      </div>
    </div>

    <!-- Live Interactive Visualizer -->
    <div class="card">
      <div class="card-title">REAL-TIME TELEMETRY & ACOUSTIC ENGINE</div>
      <div class="canvas-box">
        <canvas id="scopeCanvas"></canvas>
      </div>

      <div class="slider-container">
        <div class="control-row">
          <span>Resonant Frequency</span>
          <strong id="freqLabel" style="color:var(--cyan);font-family:monospace;">42.6 Hz</strong>
        </div>
        <input type="range" id="freqSlider" min="7" max="528" value="43" step="0.1">
      </div>

      <div class="slider-container">
        <div class="control-row">
          <span>Harmonic Gain Amplitude</span>
          <strong id="gainLabel" style="color:var(--gold);font-family:monospace;">75%</strong>
        </div>
        <input type="range" id="gainSlider" min="0" max="100" value="75">
      </div>

      <button id="toggleAudioBtn" class="btn btn-primary">
        <span>▶ Start Acoustic Synthesizer</span>
      </button>
    </div>

    <!-- Telemetry Logging -->
    <div class="card">
      <div class="card-title">RUNTIME EVENT LOGS</div>
      <div class="log-box" id="logBox">
        <div class="log-entry">> [INIT] ${appName} runtime initialized successfully.</div>
        <div class="log-entry">> [DSP] Waveform buffer synchronized at 44.1 kHz.</div>
        <div class="log-entry">> [SYSTEM] Ready for interactive testing.</div>
      </div>
      <button id="triggerPulseBtn" class="btn btn-secondary">
        ⚡ Transmit Harmonic Pulse
      </button>
    </div>

    <footer>
      Exported from Athena APK Builder • Single-File Portable HTML5
    </footer>
  </div>

  <script>
    // Audio Context Setup
    let audioCtx = null;
    let osc = null;
    let gainNode = null;
    let isPlaying = false;

    const freqSlider = document.getElementById('freqSlider');
    const freqLabel = document.getElementById('freqLabel');
    const gainSlider = document.getElementById('gainSlider');
    const gainLabel = document.getElementById('gainLabel');
    const toggleBtn = document.getElementById('toggleAudioBtn');
    const triggerPulseBtn = document.getElementById('triggerPulseBtn');
    const logBox = document.getElementById('logBox');

    function log(msg) {
      const el = document.createElement('div');
      el.className = 'log-entry';
      const time = new Date().toLocaleTimeString();
      el.textContent = `> [' + time + '] ' + msg;
      logBox.appendChild(el);
      logBox.scrollTop = logBox.scrollHeight;
    }

    function initAudio() {
      if (!audioCtx) {
        audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        gainNode = audioCtx.createGain();
        gainNode.gain.setValueAtTime(parseFloat(gainSlider.value) / 100 * 0.3, audioCtx.currentTime);
        gainNode.connect(audioCtx.destination);
      }
    }

    toggleBtn.addEventListener('click', () => {
      initAudio();
      if (!isPlaying) {
        osc = audioCtx.createOscillator();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(parseFloat(freqSlider.value), audioCtx.currentTime);
        osc.connect(gainNode);
        osc.start();
        isPlaying = true;
        toggleBtn.innerHTML = '<span>⏸ Pause Acoustic Synthesizer</span>';
        toggleBtn.style.background = '#06D6A0';
        log('Synthesizer active: ' + freqSlider.value + ' Hz');
      } else {
        if (osc) {
          osc.stop();
          osc.disconnect();
          osc = null;
        }
        isPlaying = false;
        toggleBtn.innerHTML = '<span>▶ Start Acoustic Synthesizer</span>';
        toggleBtn.style.background = 'var(--cyan)';
        log('Synthesizer stopped');
      }
    });

    freqSlider.addEventListener('input', (e) => {
      const val = parseFloat(e.target.value);
      freqLabel.textContent = val.toFixed(1) + ' Hz';
      if (isPlaying && osc) {
        osc.frequency.setValueAtTime(val, audioCtx.currentTime);
      }
    });

    gainSlider.addEventListener('input', (e) => {
      const val = parseInt(e.target.value);
      gainLabel.textContent = val + '%';
      if (gainNode && audioCtx) {
        gainNode.gain.setValueAtTime((val / 100) * 0.3, audioCtx.currentTime);
      }
    });

    triggerPulseBtn.addEventListener('click', () => {
      initAudio();
      const pulseOsc = audioCtx.createOscillator();
      const pulseGain = audioCtx.createGain();
      pulseOsc.frequency.setValueAtTime(parseFloat(freqSlider.value) * 2, audioCtx.currentTime);
      pulseGain.gain.setValueAtTime(0.4, audioCtx.currentTime);
      pulseGain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.6);
      pulseOsc.connect(pulseGain);
      pulseGain.connect(audioCtx.destination);
      pulseOsc.start();
      pulseOsc.stop(audioCtx.currentTime + 0.6);
      log('Harmonic pulse emitted: ' + (parseFloat(freqSlider.value) * 2).toFixed(1) + ' Hz');
    });

    // Canvas Oscilloscope Animation
    const canvas = document.getElementById('scopeCanvas');
    const ctx = canvas.getContext('2d');
    let phase = 0;

    function resizeCanvas() {
      canvas.width = canvas.parentElement.clientWidth * window.devicePixelRatio;
      canvas.height = canvas.parentElement.clientHeight * window.devicePixelRatio;
    }
    window.addEventListener('resize', resizeCanvas);
    resizeCanvas();

    function draw() {
      const w = canvas.width;
      const h = canvas.height;
      ctx.clearRect(0, 0, w, h);

      // Grid line
      ctx.strokeStyle = 'rgba(255,255,255,0.06)';
      ctx.lineWidth = 1;
      ctx.beginPath();
      ctx.moveTo(0, h / 2);
      ctx.lineTo(w, h / 2);
      ctx.stroke();

      // Waveform
      const freq = parseFloat(freqSlider.value);
      const amp = (parseFloat(gainSlider.value) / 100) * (h * 0.35);
      phase += isPlaying ? 0.08 : 0.02;

      ctx.strokeStyle = isPlaying ? '#06D6A0' : '#00E5FF';
      ctx.lineWidth = 3 * window.devicePixelRatio;
      ctx.beginPath();

      const points = 120;
      for (let i = 0; i <= points; i++) {
        const x = (i / points) * w;
        const norm = i / points;
        const wave = Math.sin(norm * (freq / 8) + phase) * Math.cos(norm * 2 + phase * 0.5);
        const y = (h / 2) + wave * amp;
        if (i === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();

      requestAnimationFrame(draw);
    }
    draw();
  </script>
</body>
</html>
        """.trimIndent()
    }
}
