import React, {useState} from "react";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { solarizedlight } from 'react-syntax-highlighter/dist/esm/styles/prism';
import {useSelector} from "react-redux";
import selectors from "../State/selectors";
import {Title, Text, Tabs, List} from "@mantine/core";
import NestedList from "../Components/NestedList";

const HighlightPage = () => {
    const codeFile = useSelector(selectors.selectCodeFile)["codeFile"];
    const uncovered = useSelector(selectors.selectPathCoverage)["pathCoverage"]["uncoveredPaths"];
    const colours = [
        'rgba(255, 99, 132, 0.5)',
        'rgba(255, 159, 64, 0.5)',
        'rgba(255, 205, 86, 0.5)',
        'rgba(75, 192, 192, 0.5)',
        'rgba(54, 162, 235, 0.5)',
        'rgba(153, 102, 255, 0.5)',
        'rgba(201, 203, 207, 0.5)',
    ];
    const notCoveredPaths = {};

    let highlightMsg = "";
    let inputsMsg = "";

    let counter = 0;
    // loop through all methods
    for (const method in uncovered) {
        // loop through all lines in each method's array to find uncovered branches
        for (let i = 0; i < uncovered[method].length; i++) {
            notCoveredPaths[colours[counter]] = uncovered[method][i];
            // increment counter with every new path
            counter++;
        }
    }

    const lineProps = (lineNumber) => {
        let style = {display: 'block'};

        // loop through all paths to determine the colour
        for (const path in notCoveredPaths) {
            if (notCoveredPaths[path].includes(lineNumber)) {
                style.backgroundColor = path;
            }
        }
        return {style};
    }

    // Handle text changing for line highlighting
    if (typeof uncovered === "object") {
        if (!(codeFile.length > 0)) {
            highlightMsg = "Please upload the code file again to see line highlighting.";
        }
        else if (Object.keys(notCoveredPaths).length > 0) {
            highlightMsg = "Highlighted lines were not reached/covered.";
            inputsMsg = "These paths were not covered:";
        }
        else {
            highlightMsg = "All lines were reached/covered!";
            inputsMsg = "All lines were reached/covered!";
        }
    }

    return (
        <div className="main-container">
            <div className="title-container">
                <Title order={2} padding={"md"}>Branch Coverage - Line Highlighting</Title>
            </div>
            <div>
                <Tabs defaultValue="highlight">
                    <Tabs.List>
                        <Tabs.Tab value="highlight">Line Highlighting</Tabs.Tab>
                        <Tabs.Tab value="missing">Missing Paths</Tabs.Tab>
                    </Tabs.List>

                    <Tabs.Panel value="highlight">
                        <div className="text-container">
                            <Text
                                c="red"
                                fz="lg"
                            >{highlightMsg}</Text>
                        </div>
                        <SyntaxHighlighter
                            language="java"
                            style={solarizedlight}
                            wrapLines
                            showLineNumbers
                            lineProps={lineProps}
                        >
                            {codeFile}
                        </SyntaxHighlighter>
                    </Tabs.Panel>
                    <Tabs.Panel value="missing">
                        <div className="text-container">
                            <Text c="red" fz="lg"
                            >
                                {inputsMsg}
                            </Text>
                        </div>
                        {typeof uncovered === "object" && highlightMsg.includes("Highlighted") ?
                            <div>
                                <NestedList object={uncovered} />
                            </div>
                            :
                            <></>}
                    </Tabs.Panel>
                </Tabs>
            </div>
        </div>
    );
};

export default HighlightPage;