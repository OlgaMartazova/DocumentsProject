import numpy as np
import tensorflow as tf
from keras.src.utils import img_to_array


img_size = (150, 150)


def get_doc_type(image_file):
    model = tf.keras.models.load_model('utils/model.hdf5', compile=False)
    img = image_file.resize(img_size)
    img_array = img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array /= 255.

    prediction = model.predict(img_array)
    classes = ['Миграционная карта', 'Уведомление', 'Паспорт', 'Виза']

    return classes[np.argmax(prediction)]

