import React from "react";
import {FileInput} from "@mantine/core";
import '@mantine/core/styles.css';

const HomePage = () => {
    return (
        <div>
            <FileInput
                label={"File Input"}
                description={"To get started, input your file here:"}
                placeholder={"Choose a file"}
                />
        </div>
    );
};

export default HomePage;