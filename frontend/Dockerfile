FROM node:16-alpine
WORKDIR /app
COPY . .

RUN npm update
RUN npm install react@latest react-dom@latest
RUN npm install next@12

RUN npm install --force
RUN npm run build
CMD ["npm", "run", "start"]
EXPOSE 80
