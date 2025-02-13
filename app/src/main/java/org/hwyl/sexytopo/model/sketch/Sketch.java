package org.hwyl.sexytopo.model.sketch;

import org.apache.commons.lang3.NotImplementedException;
import org.hwyl.sexytopo.control.Log;
import org.hwyl.sexytopo.control.util.Space2DUtils;
import org.hwyl.sexytopo.model.graph.Coord2D;
import org.hwyl.sexytopo.model.survey.Station;

import java.util.ArrayList;
import java.util.List;


public class Sketch extends SketchDetail {

    private List<PathDetail> pathDetails = new ArrayList<>();
    private List<SymbolDetail> symbolDetails = new ArrayList<>();
    private List<TextDetail> textDetails = new ArrayList<>();
    private List<CrossSectionDetail> crossSectionDetails = new ArrayList<>();

    private final List<SketchDetail> sketchHistory = new ArrayList<>();
    private final List<SketchDetail> undoneHistory = new ArrayList<>();

    private PathDetail activePath;
    private Colour activeColour = Colour.BLACK;

    private boolean isSaved = true;

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public void setPathDetails(List<PathDetail> pathDetails) {
        this.pathDetails = pathDetails;
        recalculateBoundingBox();
    }

    public void setSymbolDetails(List<SymbolDetail> symbolDetails) {
        this.symbolDetails = symbolDetails;
        recalculateBoundingBox();
    }

    public void setTextDetails(List<TextDetail> textDetails) {
        this.textDetails = textDetails;
        recalculateBoundingBox();
    }


    public List<PathDetail> getPathDetails() {
        return pathDetails;
    }


    public PathDetail getActivePath() {
        return activePath;
    }


    public Sketch() {
        super(Colour.NONE);
    }


    public PathDetail startNewPath(Coord2D start) {
        activePath = new PathDetail(start, activeColour);
        pathDetails.add(activePath);
        addSketchDetail(activePath);
        return activePath;
    }

    private void addSketchDetail(SketchDetail sketchDetail) {
        setSaved(false);
        sketchHistory.add(sketchDetail);
        undoneHistory.clear();
        updateBoundingBox(sketchDetail);
    }

    public void finishPath() {
        float epsilon = Space2DUtils.simplificationEpsilon(activePath);
        activePath.setPath(Space2DUtils.simplify(activePath.getPath(), epsilon));
        activePath = null;
    }

    public void addTextDetail(Coord2D location, String text, float size) {
        TextDetail textDetail = new TextDetail(location, text, activeColour, size);
        textDetails.add(textDetail);
        addSketchDetail(textDetail);
    }

    public List<SymbolDetail> getSymbolDetails() {
        return symbolDetails;
    }

    public void addSymbolDetail(Coord2D location, Symbol symbol, float size) {
        SymbolDetail symbolDetail = new SymbolDetail(location, symbol, activeColour, size);
        symbolDetails.add(symbolDetail);
        addSketchDetail(symbolDetail);
    }

    public List<TextDetail> getTextDetails() {
        return textDetails;
    }

    public void setActiveColour(Colour colour) {
        this.activeColour = colour;
    }


    public void undo() {
        if (!sketchHistory.isEmpty()) {
            SketchDetail toUndo = sketchHistory.remove(sketchHistory.size() - 1);

            if (toUndo instanceof DeletedDetail) {
                DeletedDetail deletedDetail = (DeletedDetail)toUndo;
                restoreDetailToSketch(deletedDetail.getDeletedDetail());
                for (SketchDetail sketchDetail : deletedDetail.getReplacementDetails()) {
                    removeDetailFromSketch(sketchDetail);
                }
            } else {
                removeDetailFromSketch(toUndo);
            }

            undoneHistory.add(toUndo);
        }
    }


    public void redo() {
        if (!undoneHistory.isEmpty()) {
            SketchDetail toRedo = undoneHistory.remove(undoneHistory.size() - 1);

            if (toRedo instanceof DeletedDetail) {
                DeletedDetail deletedDetail = (DeletedDetail)toRedo;
                removeDetailFromSketch(deletedDetail.getDeletedDetail());
                for (SketchDetail sketchDetail : deletedDetail.getReplacementDetails()) {
                    restoreDetailToSketch(sketchDetail);
                }
            } else {
                restoreDetailToSketch(toRedo);
            }

            sketchHistory.add(toRedo);
        }
    }


    public void deleteDetail(SketchDetail sketchDetail) {
        deleteDetail(sketchDetail, new ArrayList<>());
    }


    public void deleteDetail(
            SketchDetail sketchDetail, List<SketchDetail> replacementDetails) {
        DeletedDetail deletedDetail = new DeletedDetail(sketchDetail, replacementDetails);
        addSketchDetail(deletedDetail);
        removeDetailFromSketch(sketchDetail);
        for (SketchDetail replacementDetail : replacementDetails) {
            restoreDetailToSketch(replacementDetail);
        }
    }


    private void removeDetailFromSketch(SketchDetail sketchDetail) {
        // this is a separate function to deleteDetail because former is user-called and handles
        // undo history etc. whereas this actually removes the data
        if (sketchDetail instanceof PathDetail) {
            pathDetails.remove(sketchDetail);
        } else if (sketchDetail instanceof SymbolDetail) {
            symbolDetails.remove(sketchDetail);
        } else if (sketchDetail instanceof TextDetail) {
            textDetails.remove(sketchDetail);
        } else if (sketchDetail instanceof CrossSectionDetail) {
            crossSectionDetails.remove(sketchDetail);
        }

        recalculateBoundingBox();
    }


    public void restoreDetailToSketch(SketchDetail sketchDetail) {
        if (sketchDetail instanceof PathDetail) {
            pathDetails.add((PathDetail)sketchDetail);
        } else if (sketchDetail instanceof SymbolDetail) {
            symbolDetails.add((SymbolDetail) sketchDetail);
        } else if (sketchDetail instanceof TextDetail) {
            textDetails.add((TextDetail) sketchDetail);
        } else if (sketchDetail instanceof CrossSectionDetail) {
            crossSectionDetails.add((CrossSectionDetail)sketchDetail);
        }

        updateBoundingBox(sketchDetail);
    }


    public Coord2D findEligibleSnapPointWithin(Coord2D point, float delta) {

        Coord2D closest = null;
        float minDistance = Float.MAX_VALUE;

        for (PathDetail path : pathDetails) {

            if (activePath == path) {
                continue;
            }

            Coord2D start = path.getPath().get(0);
            Coord2D end = path.getPath().get(path.getPath().size() - 1);
            for (Coord2D coord2D : new Coord2D[]{start, end}) {
                float distance = Space2DUtils.getDistance(point, coord2D);
                if (distance < delta && distance < minDistance) {
                    closest = coord2D;
                    minDistance = distance;
                }
            }
        }
        return closest;
    }


    private List<SketchDetail> allSketchDetails() {
        List<SketchDetail> all = new ArrayList<>();
        all.addAll(pathDetails);
        all.addAll(symbolDetails);
        all.addAll(textDetails);
        all.addAll(crossSectionDetails);
        return all;
    }

    public SketchDetail findNearestDetailWithin(Coord2D point, float delta) {

        SketchDetail closest = null;
        float minDistance = Float.MAX_VALUE;

        for (SketchDetail detail : allSketchDetails()) {
            float distance = detail.getDistanceFrom(point);
            if (distance < delta && distance < minDistance) {
                closest = detail;
                minDistance = distance;
            }
        }

        return closest;
    }


    public void addCrossSection(CrossSection crossSection, Coord2D touchPointOnSurvey) {
        CrossSectionDetail sectionDetail = new CrossSectionDetail(crossSection, touchPointOnSurvey);
        crossSectionDetails.add(sectionDetail);
        addSketchDetail(sectionDetail);
    }

    public List<CrossSectionDetail> getCrossSectionDetails() {
        return crossSectionDetails;
    }

    public void setCrossSectionDetails(List<CrossSectionDetail> crossSectionDetails) {
        this.crossSectionDetails = crossSectionDetails;
    }

    public CrossSectionDetail getCrossSectionDetail(Station station) {
        // this is a bit inefficient... not sure if it's worth caching this in a map though since
        // there'll probably be max a couple of dozen x-sections per survey chunk
        for (CrossSectionDetail detail : crossSectionDetails) {
            CrossSection crossSection = detail.getCrossSection();
            if (crossSection.getStation() == station) {
                return detail;
            }
        }
        return null;
    }

    public Sketch getTranslatedCopy(Coord2D point) {
        Sketch sketch = new Sketch();

        List<PathDetail> newPathDetails = new ArrayList<>();
        for (PathDetail pathDetail : pathDetails) {
            newPathDetails.add(pathDetail.translate(point));
        }
        sketch.setPathDetails(newPathDetails);

        List<SymbolDetail> newSymbolDetails = new ArrayList<>();
        for (SymbolDetail symbolDetail : symbolDetails) {
            newSymbolDetails.add(symbolDetail.translate(point));
        }
        sketch.setSymbolDetails(newSymbolDetails);

        List<TextDetail> newTextDetails = new ArrayList<>();
        for (TextDetail textDetail : textDetails) {
            newTextDetails.add(textDetail.translate(point));
        }
        sketch.setTextDetails(newTextDetails);

        List<CrossSectionDetail> newCrossSectionDetails = new ArrayList<>();
        for (CrossSectionDetail crossSectionDetail : crossSectionDetails) {
            newCrossSectionDetails.add(crossSectionDetail.translate(point));
        }
        sketch.setCrossSectionDetails(newCrossSectionDetails);

        return sketch;
    }


    public void recalculateBoundingBox() {
        resetBoundingBox();
        for (SketchDetail sketchDetail : allSketchDetails()) {
            updateBoundingBox(sketchDetail);
        }
    }

    public void updateBoundingBox(SketchDetail sketchDetail) {
        updateBoundingBox(sketchDetail.getTopLeft());
        updateBoundingBox(sketchDetail.getBottomRight());
    }


    @Override
    public float getDistanceFrom(Coord2D point) {
        throw new NotImplementedException("Not yet implemented");
    }


    @Override
    public SketchDetail translate(Coord2D point) {
        throw new NotImplementedException("Not yet implemented");
    }
}
