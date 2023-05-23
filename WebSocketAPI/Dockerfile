FROM node:20-alpine3.16

WORKDIR /app

ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

COPY package.json ./

RUN npm install

RUN npm install -g typescript
RUN npm install -g ts-node

COPY . ./
RUN npm run build

COPY . .

EXPOSE 8080

CMD ["npm", "start"]