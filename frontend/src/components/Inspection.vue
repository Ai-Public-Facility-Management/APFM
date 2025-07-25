<template>
    <div>
        <Date
            label="CreateDate"
            v-model="value.createDate"
            :editMode="editMode"
        />
        <Boolean
            label="Isinspected"
            v-model="value.isinspected"
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
    name: 'Inspection',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "inspections",
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
