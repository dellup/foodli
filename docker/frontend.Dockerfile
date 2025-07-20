FROM node:20.19.1-bullseye AS build-stage
WORKDIR /app
COPY frontend ./frontend
WORKDIR /app/frontend

RUN npm install
RUN npm run build

FROM node:20-alpine AS runtime
WORKDIR /app

COPY --from=build-stage /app/frontend/package.json .
COPY --from=build-stage /app/frontend/package-lock.json .
RUN npm install --omit=dev

COPY --from=build-stage /app/frontend/.output .output

EXPOSE 3000
CMD ["node", ".output/server/index.mjs"]