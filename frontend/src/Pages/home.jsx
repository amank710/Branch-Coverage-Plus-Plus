import React, {useState} from "react";
import {FileInput, Text} from "@mantine/core";
import '@mantine/core/styles.css';
import {useDispatch, useSelector} from "react-redux";
import selectors from "../State/selectors";
import {setCodeFile} from "../State/Reducers/codeFileSlice";
import "../Styling/styles.css";
import FetchButton from "../Components/FetchButton";
import {setTestFile} from "../State/Reducers/testFileSlice";
import {setPathState} from "../State/Reducers/pathCoverageSlice";

const HomePage = () => {
    const dispatch = useDispatch();
    const codeFile = useSelector(selectors.selectCodeFile)["codeFile"];
    const testFile = useSelector(selectors.selectTestFile)["testFile"];
    const pathState = useSelector(selectors.selectPathCoverage)["pathState"];

    const [codeState, setCodeState] = useState(false);
    const [testState, setTestState] = useState(false);
    const [codeLoad, setCodeLoadState] = useState(false);
    const [testLoadState, setTestLoadState] = useState(false);
    const sendFiles = async (event) => {
        const formData = new FormData();
        formData.append('file', event);
        const url = "http://localhost:8080/api/paths";

        const response = await fetch(url, {
            method: "PUT",
            mode: "cors",
            body: formData
        })
            .then(response => {
                console.log(response);
                return response;
            })
            .catch(err => console.log(err));

        return !(response === undefined || response === null);
    }

    const handleFileRead = (content, fileType) => {
        // setting state for code file
        if (fileType === "code") {
            dispatch(
                setCodeFile({
                    key: codeFile,
                    codeFile: content
                })
            );
        } else if (fileType === "test") {
            dispatch(
                setTestFile({
                    key: testFile,
                    testFile: content
                })
            );
        }
    }

    const handleFileUpload = async (event, fileType) => {
        if (event === null || fileType === null) {
            console.log("Error with file selection");
            return;
        }
        if (fileType === "code") {
            setCodeLoadState(true);
        } else if (fileType === "test") {
            setTestLoadState(true);
        }
        
        const responseIsGood = await sendFiles(event);
        if (responseIsGood) {
            const reader = new FileReader();
            reader.onloadend = (event) => {
                handleFileRead(event.target.result, fileType);
            };
            reader.readAsText(event);

            dispatch(
                setPathState({
                    key: pathState,
                    pathState: false
                })
            );

            if (fileType === "code") {
                setCodeLoadState(false);
                setCodeState(true);
            } else if (fileType === "test") {
                setTestLoadState(false);
                setTestState(true);
            }
        }

    }

    return (
        <div className="main-container">
            <FileInput
                label={"Code File Input"}
                labelProps={{className: 'custom-label'}}
                radius={"sm"}
                description={"Input your code file here:"}
                placeholder={"Choose a file"}
                accept={".java"}
                onChange={file => handleFileUpload(file, "code")}
            />
            <div className="text-container">
                <Text
                    c="teal.4"
                    fw={500}
                    fz="md"
                > {codeState ? "Code file uploaded and saved." : ""} </Text>
            </div>
            <div className="text-container">
                <Text
                    c="red"
                    fw={500}
                    fz="sd"
                > {codeLoad ? "Loading..." : ""} </Text>
            </div>
            <div className="input-container">
                <FileInput
                    label={"Test File Input"}
                    labelProps={{className: 'custom-label'}}
                    radius={"sm"}
                    description={"Input your test file here (ensure it has the word 'test' in the file name):"}
                    placeholder={"Choose a file"}
                    accept={".java"}
                    onChange={file => handleFileUpload(file, "test")}
                />
            </div>
            <div className="text-container">
                <Text
                    c="teal.4"
                    fw={500}
                    fz="md"
                > {testState ? "Test file uploaded and saved." : ""} </Text>
            </div>
            <div className="text-container">
                <Text
                    c="red"
                    fw={500}
                    fz="md"
                > {testLoadState ? "Loading..." : ""} </Text>
            </div>
            <FetchButton/>
            <div className="text-container">
                <Text
                    c="teal.4"
                    fw={500}
                    fz="md"
                > {pathState ? "Data has been processed!" : ""} </Text>
            </div>
        </div>
    );
};

export default HomePage;