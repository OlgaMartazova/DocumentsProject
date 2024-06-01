from PIL import Image
import io
from flask import Flask, request, jsonify

from docs_classification_model import get_doc_type
from mrz_model import recognize_text_on_passport

app = Flask(__name__)


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


if __name__ == '__main__':
    app.run()
