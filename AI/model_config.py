from ultralytics import YOLO
import torch
import clip
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain_community.vectorstores import Chroma

# 1. HuggingFace 임베딩 모델 1회 로드
embedding_model = HuggingFaceEmbeddings(model_name="jhgan/ko-sbert-nli")

# 2. ChromaDB 연결 (persist_directory는 실제 벡터 DB 경로)
vectordb = Chroma(
    persist_directory="./chroma",
    embedding_function=embedding_model
)

# YOLO 모델 로드
yolo_model = YOLO('weights/yolo/v8/best.pt')
tracking_model = YOLO('weights/yolo/tracking/best.pt')

# 클래스 이름
class_names = [
    "Bench", "BenchBack", "Pagora", "Trench", "PavementBlock", "ConstructionCover", "StreetTreeCover",
    "RoadSafetySign", 'BoundaryStone', 'BrailleBlock', 'TreeSupport', 'FlowerStand', 'StreetLampPole',
    'SignalController', 'Manhole', 'WalkAcrossPreventionFacility', 'SoundproofWalls', 'ProtectionFence',
    'Bollard', 'TelephoneBooth', 'DirectionalSign', 'PostBox', 'BicycleRack', 'TrashCan', 'StationShelter', 'StationSign'
]

# CLIP 모델 로드
clip_device = "cuda" if torch.cuda.is_available() else "cpu"
clip_model, clip_preprocess = clip.load("ViT-B/32", device=clip_device)

# 상태 텍스트
status_texts = ["normal", "surface peeling", "damage", "frature", "distortion"]
status_tokens = clip.tokenize(status_texts).to(clip_device)
