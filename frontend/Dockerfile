FROM --platform=linux/amd64 node:18
WORKDIR /app

COPY ./public ./public
COPY ./package.json ./package.json
COPY ./.next/standalone ./
COPY ./.next/static ./.next/static

# RUN apk add --update python3 make g++ && rm -rf /var/cache/apk/*
# RUN npm update
CMD ["node", "server.js"]
EXPOSE 80
