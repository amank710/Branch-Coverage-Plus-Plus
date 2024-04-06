import {List} from "@mantine/core";
import React from "react";
import "../Styling/styles.css";

const NestedList = ({object}) => {

    return (
        <div className="list-container">
            <List withPadding={true}>
                {Object.entries(object).map(([method, value]) => (
                    <List.Item key={method} className="list-title">
                        For the method <b>{method}</b>, use the inputs:
                        <List withPadding={true}>
                            {Object.entries(value).map(([parameter, params]) => (
                                <List.Item key={parameter} className="list-item">
                                    <b>{parameter}:</b> {params.join(", ")}
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