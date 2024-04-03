import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    datasets: [],
    labels: []
};

const chartSlice = createSlice({
    name: "chart",
    initialState,
    reducers: {
        setChart(state,action) {
            state["datasets"] = action.payload.datasets;
        },
        setLabels(state, action) {
            state["labels"] = action.payload.labels;
        }
    }
});

export const {setChart, setLabels} = chartSlice.actions;
export default chartSlice.reducer;