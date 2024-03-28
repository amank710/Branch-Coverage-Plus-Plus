import React from "react";
import {FileInput} from "@mantine/core";
import '@mantine/core/styles.css';

const HomePage = () => {
    const handleFileRead = (content) => {
        console.log(content);
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
        </div>
    );
};

export default HomePage;