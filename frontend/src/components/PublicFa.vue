<template>
    <div>
        <Number
            label="CameraId"
            v-model="value.cameraId"
            :editMode="editMode"
        />
        <String
            label="Section"
            v-model="value.section"
            :editMode="editMode"
        />
        <Date
            label="InstallDate"
            v-model="value.installDate"
            :editMode="editMode"
        />
        <Date
            label="LastRepair"
            v-model="value.lastRepair"
            :editMode="editMode"
        />
        <Number
            label="Obstruction"
            v-model="value.obstruction"
            :editMode="editMode"
        />
        <type
            offline
            label="Type"
            v-model="value.type"
            :editMode="editMode"
            @change="change"
        />
        <type
            offline
            label="Status"
            v-model="value.status"
            :editMode="editMode"
            @change="change"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'PublicFa',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "publicFas",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>
