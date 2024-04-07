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
    const notCoveredLines = [];

    let highlightMsg = "";
    let inputsMsg = "";
    // Mock inputs
    let mockInputs = {
        foo: {
            x: [0, 25, 50],
        },
        test: {
            x: [0, 33, 50],
            y: [true, false]
        }
    };

    // loop through all methods
    for (const method in uncovered) {
        // loop through all lines in each method's array to find uncovered branches
        for (let i = 0; i < uncovered[method].length; i++) {
            // loop through lines per path in a method's array
            for (let j = 0; j < uncovered[method][i].length; j++) {
                notCoveredLines.push(uncovered[method][i][j]);
            }
        }
    }

    const lineProps = (lineNumber) => {
        let style = {display: 'block'};
        if (notCoveredLines.includes(lineNumber)) {
            // solarized light hex is #fdf6e3
            style.backgroundColor = "#FAE7B3";
        }
        return {style};
    }

    // Handle text changing for line highlighting
    if (typeof uncovered === "object") {
        if (!(codeFile.length > 0)) {
            highlightMsg = "Please upload the code file again to see line highlighting.";
        }
        else if (notCoveredLines.length > 0) {
            highlightMsg = "Highlighted lines were not reached/covered.";
            inputsMsg = "To cover all branches:";
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
                        <Tabs.Tab value="missing">Missing Inputs</Tabs.Tab>
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
                                <NestedList object={mockInputs} />
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