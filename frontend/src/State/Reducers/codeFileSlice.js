import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    codeFile: "",
};

const codeFileSlice = createSlice({
    name: "codeFile",
    initialState,
    reducers: {
        setCodeFile(state, action) {
            state["codeFile"] = action.payload.codeFile;
        }
    }
});

export const {setCodeFile} = codeFileSlice.actions;
export default codeFileSlice.reducer;