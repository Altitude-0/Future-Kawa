# Étape 1 : Build de l'application React avec Vite
FROM node:22-alpine AS builder

WORKDIR /app

# Copie des fichiers de dépendances
COPY package.json package-lock.json ./

# Installation propre des dépendances
RUN npm ci

# Copie du reste du code source
COPY . .

# Compilation du build de production
RUN npm run build

# Étape 2 : Serveur web Nginx pour exposer l'application
FROM nginx:alpine

# Copie de la configuration Nginx personnalisée (pour React Router)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copie des fichiers buildés depuis l'étape 1 vers le dossier public de Nginx
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
