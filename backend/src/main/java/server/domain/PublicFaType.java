package server.domain;

public enum PublicFaType {
    BENCH("벤치"),
    BENCH_BACK("등받이 벤치"),
    PAGORA("파고라"),
    TRENCH("배수로"),
    PAVEMENT_BLOCK("보도블럭"),
    CONSTRUCTION_COVER("공사장 덮개"),
    STREET_TREE_COVER("가로수 덮개"),
    ROAD_SAFETY_SIGN("도로 안전 표지판"),
    BOUNDARY_STONE("경계석"),
    BRAILLE_BLOCK("점자 블럭"),
    TREE_SUPPORT("가로수 지주대"),
    FLOWER_STAND("화분대"),
    STREET_LAMP_POLE("가로등 기둥"),
    SIGNAL_CONTROLLER("신호기 제어기"),
    MANHOLE("맨홀"),
    WALK_ACROSS_PREVENTION_FACILITY("횡단 방지 시설"),
    SOUNDPROOF_WALLS("방음벽"),
    PROTECTION_FENCE("보호 펜스"),
    BOLLARD("볼라드"),
    TELEPHONE_BOOTH("전화 부스"),
    DIRECTIONAL_SIGN("방향 표지판"),
    POST_BOX("우체통"),
    BICYCLE_RACK("자전거 거치대"),
    TRASH_CAN("쓰레기통"),
    STATION_SHELTER("정류장 쉼터"),
    STATION_SIGN("정류장 표지판"),
    FIRE_HYDRANT("소화전");

    private final String displayName;

    PublicFaType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
