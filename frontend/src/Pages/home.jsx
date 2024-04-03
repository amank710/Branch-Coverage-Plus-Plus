import React from "react";
import {Button, FileInput, Text} from "@mantine/core";
import '@mantine/core/styles.css';
import {useDispatch, useSelector} from "react-redux";
import selectors from "../State/selectors";
import {setCodeFile} from "../State/Reducers/codeFileSlice";
import "../Styling/styles.css";
import FetchButton from "../Components/FetchButton";

const HomePage = () => {
    const dispatch = useDispatch();
    const codeFile = useSelector(selectors.selectCodeFile)["codeFile"];

    const handleFileRead = (content) => {
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
        <div className="main-container">
            <FileInput
                clearable
                label={"File Input"}
                labelProps={{className: 'custom-label'}}
                radius={"sm"}
                description={"To get started, input your file here:"}
                placeholder={"Choose a file"}
                onChange={file => handleFileUpload(file)}
            />
            <div className="text-container">
                <Text
                    c="dimmed"
                    fz="md"
                > {codeFile.length > 0 ? "File uploaded and saved." : ""} </Text>
            </div>
            <FetchButton />
        </div>
    );
};

export default HomePage;