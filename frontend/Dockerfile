FROM --platform=linux/amd64 node:18
WORKDIR /app
COPY . .

# RUN apk add --update python3 make g++ && rm -rf /var/cache/apk/*
# RUN npm update
RUN npm install --legacy-peer-deps

RUN npm run build
CMD ["npm", "run", "start"]
EXPOSE 80
