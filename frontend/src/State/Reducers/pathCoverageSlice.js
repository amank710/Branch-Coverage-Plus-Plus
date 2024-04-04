import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    pathCoverage: {
        pathCoverageScore: 0,
        lineHits: {},
        uncoveredPaths: {}
    }
};

const pathCoverageSlice = createSlice({
    name: "pathCoverage",
    initialState,
    reducers: {
        setPathCoverage(state, action) {
            state["pathCoverage"] = action.payload.pathCoverage;
        }
    }
});

export const {setPathCoverage} = pathCoverageSlice.actions;
export default pathCoverageSlice.reducer;