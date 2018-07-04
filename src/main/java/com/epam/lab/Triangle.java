package com.epam.lab;

import com.epam.lab.custom.exception.BuildTriangleException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Triangle {

    private static final Logger LOG = LogManager.getLogger(Triangle.class);
    private static final int TRIANGLE_SIDES_COUNT = 3;

    private ArrayList<Integer> triangleSides;

    public Triangle() {
        LOG.debug("debug logger");
        LOG.info("info logger");
        LOG.warn("warn logger");
        LOG.error("error");
        LOG.fatal("fatal logger");
        triangleSides = new ArrayList<Integer>();
    }

    public void addTriangleSide(Integer triangleSide) {
        this.triangleSides.add(triangleSide);
        LOG.info("side with size = " + triangleSide + " was added");
    }

    public void buildTriangle() throws BuildTriangleException {
        if (this.triangleSides.size() < TRIANGLE_SIDES_COUNT) {
            LOG.error("There are less than 3 side for triangle");
            throw new BuildTriangleException("Triangle cannot have less than 3 sides!");
        }
        else {
            int maxSideIndex = getMaxSideIndex();
            int lessSidesSum = 0;
            for (int i = 0; i < this.triangleSides.size(); i++) {
                if (i != maxSideIndex)
                    lessSidesSum += this.triangleSides.get(i);
            }
            if (this.triangleSides.get(maxSideIndex) >= lessSidesSum) {
                LOG.info("Wrong triangle's sides proportions");
                throw new BuildTriangleException("Wrong triangle's sides proportions!");
            }
        }
    }

    private int getMaxSideIndex() {
        int maxSide = 0;
        int maxSideIndex = 0;
        for (Integer side : this.triangleSides) {
            if (side > maxSide) {
                maxSide = side;
                maxSideIndex = this.triangleSides.indexOf(side);
            }
        }
        LOG.info("maxSideIndex was successfully returned");
        return maxSideIndex;
    }
}
