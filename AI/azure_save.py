from azure.storage.blob import BlobServiceClient
import os
from datetime import datetime


def savefile(file_obj, file_type):
    
    AZURE_CONNECTION_STRING=''
    CONTAINER_NAME='filecontainer'
    
    blob_service_client = BlobServiceClient.from_connection_string(AZURE_CONNECTION_STRING)
    container_client = blob_service_client.get_container_client(CONTAINER_NAME)

    import uuid
    if file_type.lower() == '.png':
        blob_name = "image-" + str(uuid.uuid4())
        blob_path = "images/" + blob_name + file_type
    elif file_type.lower() == '.pdf':
        blob_name = "report_" + datetime.today().strftime("%Y-%m-%d")
        blob_path = "reports/" + blob_name + file_type
    elif file_type.lower() == '.docx':
        blob_name = "proposal-" + str(uuid.uuid4())
        blob_path = "proposals/" + blob_name + file_type
    else:
        raise ValueError("지원하지 않는 파일 타입입니다.")

    blob_client = container_client.get_blob_client(blob_path)

    # 파일 객체를 직접 읽어 업로드 (BytesIO 등 바이너리 스트림 지원)
    blob_client.upload_blob(file_obj, overwrite=True)

    return blob_client.url


