<template>
    <div>
        <Date
            label="CreationDate"
            v-model="value.creationDate"
            :editMode="editMode"
        />
        <Photo
            offline
            label="Content"
            v-model="value.content"
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
    name: 'Report',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "reports",
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
