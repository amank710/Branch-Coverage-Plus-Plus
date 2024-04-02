import React from "react";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { solarizedlight } from 'react-syntax-highlighter/dist/esm/styles/prism';
import {useSelector} from "react-redux";
import selectors from "../State/selectors";

const HighlightPage = () => {
    const codeFile = useSelector(selectors.selectCodeFile)["codeFile"];

    const codeString = `function add(a, b) {
        return a + b;
    }`;

    const notCoveredLines = [2, 3];

    const lineProps = (lineNumber) => {
        let style = {display: 'block'};
        if (notCoveredLines.includes(lineNumber)) {
            style.backgroundColor = "#FFDB81";
        }
        return {style};
    }

    return (
        <div>
            <p>Insert line highlighting here</p>
            <SyntaxHighlighter
                language="javascript"
                style={solarizedlight}
                wrapLines
                showLineNumbers
                lineProps={lineProps}
            >
                {codeString}
            </SyntaxHighlighter>
            <p>{codeFile}</p>
        </div>
    );
};

export default HighlightPage;