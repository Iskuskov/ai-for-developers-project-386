# Changelog

## 1.0.0 (2026-11-14)


### Features

* add landing page with link to booking page stub ([cc82084](https://github.com/Iskuskov/ai-for-developers-project-386/commit/cc820849a88d99a0690b0e6fc1315806e8271f05))
* **backend:** GET /bookings — предстоящие Записи всех типов одним списком ([#19](https://github.com/Iskuskov/ai-for-developers-project-386/issues/19)) ([18a2e2f](https://github.com/Iskuskov/ai-for-developers-project-386/commit/18a2e2f833cc33029ee7271535128f60e2543388))
* **backend:** GET /event-types по контракту и тесты ([#15](https://github.com/Iskuskov/ai-for-developers-project-386/issues/15)) ([680b6cc](https://github.com/Iskuskov/ai-for-developers-project-386/commit/680b6cc30ac01cd08fa530fcd205a44f9f1f9ab5))
* **backend:** GET /event-types/{id} — Тип встречи по идентификатору, 404 для неизвестного ([#14](https://github.com/Iskuskov/ai-for-developers-project-386/issues/14)) ([06e5321](https://github.com/Iskuskov/ai-for-developers-project-386/commit/06e5321809814f8bb77645acec0b49327e59c05a))
* **backend:** POST /bookings — создание Записи с непересекающимися интервалами ([#18](https://github.com/Iskuskov/ai-for-developers-project-386/issues/18)) ([187db2d](https://github.com/Iskuskov/ai-for-developers-project-386/commit/187db2db15f0320ca45081b0c948326b2c298f59))
* **backend:** POST /event-types — валидация и создание Типа встречи ([#16](https://github.com/Iskuskov/ai-for-developers-project-386/issues/16)) ([b85efff](https://github.com/Iskuskov/ai-for-developers-project-386/commit/b85efffbf5a44ef5f41a2c9fb0bc1f6777723d90))
* **backend:** вычисление свободных слотов GET /slots ([#17](https://github.com/Iskuskov/ai-for-developers-project-386/issues/17)) ([a4cc888](https://github.com/Iskuskov/ai-for-developers-project-386/commit/a4cc8886d1134ba25a81dcadcdcb184e69334afc))
* **backend:** ограничение имени гостя — максимум 100 символов по контракту ([#14](https://github.com/Iskuskov/ai-for-developers-project-386/issues/14)) ([4551ddc](https://github.com/Iskuskov/ai-for-developers-project-386/commit/4551ddc7f3a3a4270c3058c1c9750cebd645e708))
* **frontend:** страница /booking на сгенерированном SDK ([#15](https://github.com/Iskuskov/ai-for-developers-project-386/issues/15)) ([71b5302](https://github.com/Iskuskov/ai-for-developers-project-386/commit/71b5302af31524fc4d0eeb79091a46478022798f))
* **frontend:** страница /event-types с формой создания на SDK ([#16](https://github.com/Iskuskov/ai-for-developers-project-386/issues/16)) ([8b95e32](https://github.com/Iskuskov/ai-for-developers-project-386/commit/8b95e3243cbc2617dbcb3295029a7099fea04938))
* **frontend:** страница Записей /bookings и ссылка «Записи» в шапке ([#19](https://github.com/Iskuskov/ai-for-developers-project-386/issues/19)) ([a8758f8](https://github.com/Iskuskov/ai-for-developers-project-386/commit/a8758f870c16ea579e5f6eddb273564549c7bf69))
* **frontend:** страница календаря слотов /booking/:eventTypeId ([#17](https://github.com/Iskuskov/ai-for-developers-project-386/issues/17)) ([a633985](https://github.com/Iskuskov/ai-for-developers-project-386/commit/a63398505910186d6b0adc218a9ccadb8efea996))
* **frontend:** форма записи под календарём слотов с инлайн-подтверждением ([#18](https://github.com/Iskuskov/ai-for-developers-project-386/issues/18)) ([c2222c5](https://github.com/Iskuskov/ai-for-developers-project-386/commit/c2222c5256fa7d4a20f95dbdb59deeaa2096d4cc))
* scaffold app skeleton with checks and CI ([2b8f727](https://github.com/Iskuskov/ai-for-developers-project-386/commit/2b8f727784865a5b80173f6523380c93e5923c99))
* **spec:** POST /event-types в контракте TypeSpec и SDK ([#16](https://github.com/Iskuskov/ai-for-developers-project-386/issues/16)) ([a479ce1](https://github.com/Iskuskov/ai-for-developers-project-386/commit/a479ce19f2174bbae12843f41f2d18badd7e3418))
* **spec:** контракт GET /bookings и регенерация SDK ([#19](https://github.com/Iskuskov/ai-for-developers-project-386/issues/19)) ([4079c75](https://github.com/Iskuskov/ai-for-developers-project-386/commit/4079c75d6cf9bf9c3073357305751ded55c0a57c))
* **spec:** контракт GET /slots и регенерация SDK ([#17](https://github.com/Iskuskov/ai-for-developers-project-386/issues/17)) ([cea12e4](https://github.com/Iskuskov/ai-for-developers-project-386/commit/cea12e413b4b295e970f8b8c456c5448f49eb3ec))
* **spec:** контракт POST /bookings и регенерация SDK ([#18](https://github.com/Iskuskov/ai-for-developers-project-386/issues/18)) ([4d1038b](https://github.com/Iskuskov/ai-for-developers-project-386/commit/4d1038b9a9037fc21345860205d75f3b8f1aede2))
* **spec:** контракт Типа встречи в TypeSpec и цепочка генерации ([#15](https://github.com/Iskuskov/ai-for-developers-project-386/issues/15)) ([f289883](https://github.com/Iskuskov/ai-for-developers-project-386/commit/f28988391ed3591ac453518bebbe911f93078243))
* главная страница и заглушка страницы записи (шаг 4) ([5a1627b](https://github.com/Iskuskov/ai-for-developers-project-386/commit/5a1627b677063313c70a8b26ac18d9f0c23e2528))


### Bug Fixes

* **backend:** даты вне 14-дневного окна записи — пустой список слотов ([#14](https://github.com/Iskuskov/ai-for-developers-project-386/issues/14)) ([84fae2a](https://github.com/Iskuskov/ai-for-developers-project-386/commit/84fae2a979e8bb3566b42ee03cf3b4cd3cb0f53e))
* **frontend:** «сегодня» в календаре слотов считается в Europe/Moscow ([#14](https://github.com/Iskuskov/ai-for-developers-project-386/issues/14)) ([0cba1ce](https://github.com/Iskuskov/ai-for-developers-project-386/commit/0cba1ce85a36eb3b4262ef3f49bdc4bec08d8abc))
* **frontend:** нейминг по глоссарию — «Типы встреч» вместо «Видов брони» ([#14](https://github.com/Iskuskov/ai-for-developers-project-386/issues/14)) ([c7e2f9e](https://github.com/Iskuskov/ai-for-developers-project-386/commit/c7e2f9ee369586b0499527944c9aa0ab7dfbc575))
