# Проект по распознаванию объектов на фотографиях документов
 
#### DocsApp2 - Android-приложение
- `app-debug.apk` - файл установки мобильного приложения, тестовый вариант

#### Методы для взаимодействия сервера с мобильным клиентом:

- Отправка файла с изображением документа. 
`POST["analyze_image"]`, вернуть JSON 
```
{
    "id": 1,
    "docType": "Паспорт",
    "fileName": "4637844.jpg",
    "result": {
        "dateOfBirth": "123456",
        "dateOfExpiry": "123456",
        "documentNumber": "123456789",
        "documentType": "P",
        "firstName": "name",
        "issuingState": "ABC",
        "lastName": "lastname",
        "nationality": "ABC",
        "sex": "F"
    }
}
```
- Верификация токена авторизации. Сервер принимает токен типа String, возвращает True если авторизован, False если не авторизован.
    
    `GET["verify_token"]`

- Начать новый пакет. 
    
    `GET["start_new_package"]`. Авторизованная зона, Bearer Token в Header.

- Завершить пакет.

     `GET["end_package"]`. Авторизованная зона, Bearer Token в Header.

- Обработать результат распознавания. 

    `GET["process_result]` с параметрами Int id, Boolean saveResult. Id распознанного объекта, saveResult True, если распознанный документ сохранить, saveResult False, если распознанный документ не сохранять.



#### Метод для отправки запроса аналиха изображения:
```
@app.route('/analyze_image', methods=['POST'])
def analyze_image():
    # Check if the request contains a file
    if 'image' not in request.files:
        return jsonify({'error': 'No image provided'})

    image_file = request.files['image']
    image_read = image_file.read()
    image = Image.open(io.BytesIO(image_read))

    doc_type = get_doc_type(image)
    keys = ('docType', 'fileName', 'result')
    result_dict = dict.fromkeys(keys)
    result_dict['docType'] = doc_type
    result_dict['fileName'] = image_file.filename

    if doc_type == 'Паспорт' or doc_type == 'Виза':
        recognized_data = recognize_text_on_passport(image_read)
        result_dict['result'] = recognized_data

    print(result_dict)

    return jsonify(result_dict)
```

#### слой нейронной сети
- `docs_classification_model.py` для определения типа документа, `get_doc_type(image)`

- `mrz_model.py` для распознавания объектов на документах с машиночитаемым кодом, `recognize_text_on_passport(image_read)`

- файл `model.hdf5` используется для классификации изображений, обученная модель

- На сервере запустить `pip install -r requirements.txt` для установки всех библиотек