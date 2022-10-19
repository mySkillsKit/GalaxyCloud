# netology-diplom-frontend

## Вариант запуск с помощью Dockerfile отдельно front `http://localhost:8080/login`
- Создаем образ из нашего Dockerfile, мы должны запустить: `docker build --tag=app-front:latest .`
- Запускаем контейнер из нашего образа: `docker run --rm -p8080:8080 -it app-front`


## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
