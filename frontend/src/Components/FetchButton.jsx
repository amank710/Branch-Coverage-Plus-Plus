import {useDispatch, useSelector} from "react-redux";
import selectors from "../State/selectors";
import {Button} from "@mantine/core";
import React from "react";
import "../Styling/styles.css";
import {setPathCoverage} from "../State/Reducers/pathCoverageSlice";
import {setChart, setLabels} from "../State/Reducers/chartSlice";

const FetchButton = () => {
    const dispatch = useDispatch();
    const pathCovObject = useSelector(selectors.selectPathCoverage)["pathCoverage"];
    const datasets = useSelector(selectors.selectChart)["datasets"];
    const labels = useSelector(selectors.selectChart)["labels"];

    const bgColorMapping = [
        'rgba(255, 99, 132, 0.2)',
        'rgba(255, 159, 64, 0.2)',
        'rgba(255, 205, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(201, 203, 207, 0.2)',
    ];
    const borderColorMapping = [
        'rgba(255, 99, 132, 1)',
        'rgba(255, 159, 64, 1)',
        'rgba(255, 205, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(201, 203, 207, 1)',
    ];

    const getChartInput = async () => {
        await getData();
    }

    const getData = async () => {
        await fetch("http://localhost:8080/api/paths", {
            method: "GET",
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
            }
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                setPathCovValues(data);
            })
            .catch(err => console.log(err));
    }

    const setPathCovValues = (data) => {
        dispatch(
            setPathCoverage({
                key: pathCovObject,
                pathCoverage: data
            })
        );
        setChartData(data);
    }

    const setChartData = (data) => {
        const allLabels = [];
        const totalData = [];
        const bgColors = [];
        const borderColors = [];
        let counter = 0;

        // looping through line hits map
        for (const method in data.lineHits) {
            // looping through each individual method's map
            for (const line in data.lineHits[method]) {
                // pushing number of hits
                totalData.push(data.lineHits[method][line]);
                allLabels.push(method + "-Line:" + line);
                bgColors.push(bgColorMapping[counter]);
                borderColors.push(borderColorMapping[counter]);
            }

            counter++;
        }

        // creating the dataset object
        const dataset = [{
            label: 'Path Coverage',
            data: totalData,
            backgroundColor: bgColors,
            borderColor: borderColors,
            borderWidth: 1
        }];

        // dispatching to redux store
        dispatch(
            setChart({
                key: datasets,
                datasets: dataset,
            })
        );
        dispatch(
            setLabels({
                key: labels,
                labels: allLabels
            })
        );
    }

    return (
        <div className="button-container">
            <Button
                size={"compact-md"}
                onClick={getChartInput}
                radius={"md"}
            >Get Data</Button>
        </div>
    );
};

export default FetchButton;