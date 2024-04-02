import React from "react";
import {FileInput} from "@mantine/core";
import '@mantine/core/styles.css';
import {useDispatch, useSelector} from "react-redux";
import selectors from "../State/selectors";
import {setCodeFile} from "../State/Reducers/codeFileSlice";

const HomePage = () => {
    const dispatch = useDispatch();
    const codeFile = useSelector(selectors.selectCodeFile)["codeFile"];

    const handleFileRead = (content) => {
        console.log(content);
        // setting state for code file
        dispatch(
            setCodeFile({
                key: codeFile,
                codeFile: content
            })
        );
        // can pass content to backend here or something
    }

    const handleFileUpload = (event) => {
        const reader = new FileReader();
        reader.onloadend = (event) => {
            handleFileRead(event.target.result);
        };
        reader.readAsText(event);
    }

    return (
        <div>
            <FileInput
                clearable
                label={"File Input"}
                description={"To get started, input your file here:"}
                placeholder={"Choose a file"}
                onChange={file => handleFileUpload(file)}
            />
            <p>{codeFile}</p>
        </div>
    );
};

export default HomePage;