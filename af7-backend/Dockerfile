# Imagen base Node.js LTS ligera
FROM node:20-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar dependencias primero (aprovecha la caché de Docker)
COPY package*.json ./
RUN npm install --omit=dev

# Copiar el resto del código
COPY . .

# Exponer el puerto del servidor Express
EXPOSE 3000

# Comando de arranque
CMD ["node", "server.js"]
