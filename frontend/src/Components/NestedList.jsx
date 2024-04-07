import {List} from "@mantine/core";
import React from "react";
import "../Styling/styles.css";

const NestedList = ({object}) => {

    return (
        <div className="list-container">
            <List withPadding={true}>
                {Object.entries(object).map(([method, value]) => (
                    <List.Item key={method} className="list-title">
                        Method <b>{method}</b> is missing these paths (shown by line numbers):
                        <List withPadding={true}>
                            {Object.entries(value).map(([parameter, params]) => (
                                <List.Item key={parameter} className="list-item">
                                    <b>Path {parameter}:</b> {params.join(", ")}
                                </List.Item>
                            ))}
                        </List>
                    </List.Item>
                ))}
            </List>
            <br></br>
        </div>
    );
}

export default NestedList;