FROM node:16-alpine
WORKDIR /app
COPY . .

# RUN apk add --update python3 make g++ && rm -rf /var/cache/apk/*
RUN npm update
# RUN npm install react@latest react-dom@latest
# RUN npm install next@12

RUN npm install
RUN npm run build
CMD ["npm", "run", "start"]
EXPOSE 80
