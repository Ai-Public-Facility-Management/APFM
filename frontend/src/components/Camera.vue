<template>
    <div>
        <String
            label="Location"
            v-model="value.location"
            :editMode="editMode"
        />
        <String
            label="Ip"
            v-model="value.ip"
            :editMode="editMode"
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
    name: 'Camera',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "cameras",
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
