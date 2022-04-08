package com.example.blindpersonassisstantfyp;

import java.util.ArrayList;

class Caption{
    public String text;
    public double confidence;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}

class Description{
    public ArrayList<String> tags;
    public ArrayList<Caption> captions;

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<Caption> getCaptions() {
        return captions;
    }
}

class Metadata{
    public int height;
    public int width;
    public String format;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getFormat() {
        return format;
    }
}

public class AzureServerResponse{
    public Description description;
    public String requestId;
    public Metadata metadata;
    public String modelVersion;

    public Description getDescription() {
        return description;
    }

    public String getRequestId() {
        return requestId;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getModelVersion() {
        return modelVersion;
    }
}