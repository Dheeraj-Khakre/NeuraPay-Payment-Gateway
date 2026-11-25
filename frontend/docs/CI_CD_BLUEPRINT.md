# NeuraPay Frontend CI/CD Blueprint

The workflow is `.github/workflows/frontend-ci-cd.yml`. It filters changes to `frontend/**` and the workflow itself, so the Spring Boot application is neither built nor deployed.

1. **Checkout:** obtains the repository and frontend source.
2. **Node setup:** installs Node.js 20 with npm cache.
3. **Install:** runs `npm ci` from `frontend`.
4. **Lint:** runs `npm run lint --if-present`; lint is advisory until an Angular lint target is added.
5. **Test:** runs Angular/Karma tests once in Chrome Headless.
6. **Production build:** produces the optimized Angular SPA.
7. **Docker build:** creates an independent Nginx image.
8. **Basic validation:** starts the image on port 8088 and verifies `/`.
9. **Registry push:** commented placeholder requiring `REGISTRY_URL`, `REGISTRY_USERNAME`, and `REGISTRY_PASSWORD` secrets.
10. **Deployment:** intentionally disabled and ready to be replaced with ECS, AKS, Kubernetes, Azure Static Web Apps, or another platform.

Configure registry secrets in GitHub Actions settings. Never commit credentials or API secrets. The backend is not part of this pipeline.
