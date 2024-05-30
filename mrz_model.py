import matplotlib.pyplot as plt
import cv2
import numpy
from imutils.perspective import four_point_transform
from paddleocr import PaddleOCR, draw_ocr  # main OCR dependencies
import easyocr

reader = easyocr.Reader(['en'])
paddle_ocr_model_new = PaddleOCR(lang='en', use_angle_cls=True, use_gpu=False)


def detect_face(image):
    img_copy = image.copy()
    img_gray = cv2.cvtColor(img_copy, cv2.COLOR_BGR2GRAY)

    face_classifier = cv2.CascadeClassifier(
        cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
    )

    faces = face_classifier.detectMultiScale(img_gray, scaleFactor=1.3, minNeighbors=5)
    for (x, y, w, h) in faces:
        cv2.rectangle(img_copy, (x, y), (x + w, y + h), (0, 255, 0), 4)
        plt.imshow(img_copy)
        plt.show()
        return image


def auto_rotate(image):
    gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    # gray = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    blur = cv2.GaussianBlur(gray, (11, 11), 0)
    # thresh = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]
    thresh = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]

    # Find contours and sort for largest contour
    cnts = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if len(cnts) == 2 else cnts[1]
    cnts = sorted(cnts, key=cv2.contourArea, reverse=True)

    print("Number of Contours found = " + str(len(cnts)))
    displayCnt = None
    for c in cnts:
        # Perform contour approximation
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.1 * peri, True)
        if len(approx) == 4:
            displayCnt = approx
            break

    # Obtain birds' eye view of image
    if displayCnt is not None and displayCnt.any():
        warped = four_point_transform(image, displayCnt.reshape(4, 2))
    else:
        cv2.drawContours(image, cnts, -1, (0, 255, 0), 3)
        return image, displayCnt

    # cv2.drawContours(image, cnts, -1, (0, 255, 0), 2)

    # plt.imshow(thresh)
    # plt.show()

    return warped, displayCnt


def ocr_on_selection_easy(image):
    return reader.readtext(image, detail=0)


def mrz_postprocess(paddle_mrz, easy_mrz):
    # first_line_data = paddle_mrz[0]
    # if first_line_data != "":
    #     passport_type = first_line_data[:2]
    #     if passport_type[-1] == '<': passport_type = passport_type[0]
    #     issuing_state = first_line_data[2:5]
    #
    #     index_last_name = first_line_data[5:].find('<<')
    #     last_name = first_line_data[5:5 + index_last_name].split('<')
    #     last_name = ' '.join(last_name)
    #
    #     index_first_name = first_line_data[7 + index_last_name:].find('<<')
    #     first_name = first_line_data[7 + index_last_name: 7 + index_last_name + index_first_name].split('<')
    #     first_name = ' '.join(first_name)
    # else:
    first_line_data = easy_mrz[0]
    if first_line_data != "":
        passport_type = first_line_data[:2]
        if passport_type[-1] == '<': passport_type = passport_type[0]
        issuing_state = first_line_data[2:5]

        index_last_name = first_line_data[5:].find('<<')
        last_name = first_line_data[5:5 + index_last_name].split('<')
        last_name = ' '.join(last_name)

        index_first_name = first_line_data[7 + index_last_name:].find('<<')
        first_name = first_line_data[7 + index_last_name: 7 + index_last_name + index_first_name].split('<')
        first_name = ' '.join(first_name)
    else:
        passport_type = ""
        issuing_state = ""
        last_name = ""
        first_name = ""

    # second_line_data = paddle_mrz[1]
    # if second_line_data != "":
    #     passport_number = second_line_data[0:9].replace('<', '')
    #     nationality = second_line_data[10:13]
    #     date_of_birth = second_line_data[13:19]
    #     sex = second_line_data[20:21]
    #     date_of_expiry = second_line_data[21:27]
    # else:
    second_line_data = easy_mrz[1]
    if second_line_data != "":
        passport_number = second_line_data[0:9].replace('<', '')
        nationality = second_line_data[10:13]
        date_of_birth = second_line_data[13:19]
        sex = second_line_data[20:21]
        date_of_expiry = second_line_data[21:27]
    else:
        passport_number = ""
        nationality = ""
        date_of_birth = ""
        sex = ""
        date_of_expiry = ""

    if sex == "7":
        sex = "F"
    if sex == "0":
        sex = "M"
    return [passport_type, issuing_state, last_name, first_name, passport_number, nationality, date_of_birth, sex,
            date_of_expiry]


def resize_image(image):
    (img_h, img_w, _) = image.shape
    if img_h > img_w:
        image = image[round(img_h / 2): img_h, 0:img_w]
    resized_image = cv2.resize(image, (800, 600))
    plt.imshow(resized_image)
    plt.show()
    return resized_image


def get_dilation(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # --- performing Otsu threshold ---
    ret, thresh1 = cv2.threshold(gray, 0, 255, cv2.THRESH_OTSU | cv2.THRESH_BINARY_INV)

    # --- choosing the right kernel
    # --- kernel size of 3 rows (to join dots above letters 'i' and 'j')
    # --- and 10 columns to join neighboring letters in words and neighboring words
    rect_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (20, 20))
    dilation = cv2.dilate(thresh1, rect_kernel, iterations=1)
    return dilation


def sort_contours(cnts, method="top-to-bottom"):
    # initialize the reverse flag and sort index
    reverse = False
    i = 0
    # handle if we need to sort in reverse
    if method == "right-to-left" or method == "bottom-to-top":
        reverse = True
    # handle if we are sorting against the y-coordinate rather than
    # the x-coordinate of the bounding box
    if method == "top-to-bottom" or method == "bottom-to-top":
        i = 1
    # construct the list of bounding boxes and sort them from top to
    # bottom
    boundingBoxes = [cv2.boundingRect(c) for c in cnts]
    (cnts, boundingBoxes) = zip(*sorted(zip(cnts, boundingBoxes),
                                        key=lambda b: b[1][i], reverse=reverse))
    # return the list of sorted contours and bounding boxes
    return cnts, boundingBoxes


def detect_mrz(image):
    dilation = get_dilation(image)
    # ---Finding contours ---
    contours, hierarchy = cv2.findContours(dilation, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    (cnts, boundingBoxes) = sort_contours(contours)
    # cnts = sorted(contours, key=cv2.contourArea, reverse=True)
    im2 = image.copy()
    paddle_recognized_texts = []
    easy_recognized_texts = []
    for cnt in cnts:
        x, y, w, h = cv2.boundingRect(cnt)
        # print(ocr_on_selection_easy(im2[y:y+h, x:x + w]))
        # w > 600 and
        if w > 600 and y > im2.shape[0] / 2:
            #   # cv2.imwrite(f'passport_{h}.png', im2[y:y+h, x:x + w])
            img_select = cv2.cvtColor(im2[y:y + h, x:x + w], cv2.COLOR_BGR2RGB)
            plt.imshow(img_select)
            plt.show()
            # paddle_recognized_texts.append(paddle_get_result(img_select))
            easy_recognized_texts.append(ocr_on_selection_easy(img_select))
        # cv2.rectangle(im2, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # plt.imshow(im2)
    # plt.show()
    return paddle_recognized_texts, easy_recognized_texts


def paddle_recognize(dim, img_roi):
    (x, y, w, h) = dim
    img_rect = cv2.rectangle(img_roi, (x, y), (x + w, y + h), (0, 0, 0))
    img_select = img_roi[y:y + h, x:x + w]
    image_rgb = cv2.cvtColor(img_select, cv2.COLOR_BGR2RGB)
    return paddle_get_result(image_rgb)


def paddle_get_result(image):
    result = paddle_ocr_model_new.ocr(image)
    print(result)
    recognized_texts = []
    inner_result = result[0]
    if inner_result is not None:
        for res in inner_result:
            recognized_texts.append(res[1][0])
    return recognized_texts


def recognize_text_on_passport(image_file):
    image = cv2.imdecode(numpy.fromstring(image_file, numpy.uint8), cv2.IMREAD_UNCHANGED)

    if detect_face(image) is None:
        image = cv2.rotate(image, cv2.ROTATE_90_CLOCKWISE)
        if detect_face(image) is None:
            image = cv2.rotate(image, cv2.ROTATE_90_CLOCKWISE)
            if detect_face(image) is None:
                image = cv2.rotate(image, cv2.ROTATE_90_CLOCKWISE)

    if detect_face(image) is None:
        return None

    rotated_image, displayCnt = auto_rotate(image)
    if displayCnt is None or not displayCnt.any():
        return None
    plt.imshow(rotated_image)
    plt.show()

    resized_image = resize_image(rotated_image)

    paddle_array, easy_array = detect_mrz(resized_image)
    paddle_mrz = [item for sublist in paddle_array for item in sublist]
    easy_mrz = [item for sublist in easy_array for item in sublist]
    print(paddle_mrz)
    print(easy_mrz)
    result = mrz_postprocess(paddle_mrz, easy_mrz)

    data_array = ['documentType', 'issuingState', 'lastName', 'firstName', 'documentNumber', 'nationality',
                  'dateOfBirth', 'sex', 'dateOfExpiry']

    return {key: value for key, value in zip(data_array, result)}
