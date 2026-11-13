import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  // Serves /data/*.json from the public folder
  publicDir: 'public',
})
