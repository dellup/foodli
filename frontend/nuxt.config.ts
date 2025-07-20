// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  runtimeConfig: {
    public: {
      apiBase: '/api'
    }
  },
  nitro: {
    routeRules: {
      '/api/**': { 
        proxy: { 
          to: 'http://localhost:8080/**'
        }
      }
    }
  },
})
