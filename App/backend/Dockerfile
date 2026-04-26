FROM node:20-slim

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

ENV MONGO_URI=mongodb://admin:password@mongo:27017/mindsky?authSource=admin
ENV AI_URL_START=http://ai:8080/api/gateway/start
ENV AI_URL_ANSWER=http://ai:8080/api/gateway/answer
ENV CALL_API_URL=http://emergency:8084/crisis/call?to=%2B91
ENV SMS_API_URL=http://emergency:8084/crisis/sendSms?to=%2B91

EXPOSE 5001

CMD ["npm", "start"]
