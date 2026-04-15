package com.iomt.federated;

import com.iomt.dao.FederatedDAO;
import com.iomt.model.FederatedUpdate;
import java.util.*;

/**
 * FederatedLearning - Simulated Federated Learning System
 * Simulates distributed model training across IoMT devices:
 * 1. Each device trains locally with its own data (simulated)
 * 2. Only model weights are shared (not raw data)
 * 3. Central server aggregates weights (Federated Averaging)
 * 4. Global model is distributed back
 *
 * This is a simulation - actual ML is replaced with random weights
 * and simple averaging to demonstrate the federated learning concept
 */
public class FederatedLearning {

    private FederatedDAO federatedDAO;
    private static final int WEIGHT_COUNT = 10;  // Number of model weights
    private static final String[] DEVICE_IDS = {
        "ECG-Monitor-01", "BP-Sensor-02", "SpO2-Device-03",
        "Temp-Sensor-04", "Glucose-Monitor-05"
    };

    private static final Random random = new Random();

    public FederatedLearning() {
        this.federatedDAO = new FederatedDAO();
    }

    public FederatedLearning(FederatedDAO dao) {
        this.federatedDAO = dao;
    }

    /**
     * Simulate local training on a device
     * Generates random weight values simulating a trained local model
     *
     * @param deviceId The device performing local training
     * @param roundNumber Current training round
     * @return FederatedUpdate with local weights
     */
    public FederatedUpdate simulateLocalTraining(String deviceId, int roundNumber) {
        // Generate simulated local model weights
        double[] weights = new double[WEIGHT_COUNT];
        StringBuilder weightsJson = new StringBuilder("[");

        for (int i = 0; i < WEIGHT_COUNT; i++) {
            // Simulate training: base weight + noise (representing device-specific data characteristics)
            weights[i] = Math.round((0.5 + random.nextGaussian() * 0.2) * 10000.0) / 10000.0;
            if (i > 0) weightsJson.append(",");
            weightsJson.append(String.format("%.4f", weights[i]));
        }
        weightsJson.append("]");

        // Simulate accuracy (improves slightly with each round)
        double baseAccuracy = 0.65 + (roundNumber * 0.03);
        double accuracy = Math.min(0.98, baseAccuracy + random.nextGaussian() * 0.05);
        accuracy = Math.round(accuracy * 10000.0) / 10000.0;

        FederatedUpdate update = new FederatedUpdate(deviceId, weightsJson.toString(), roundNumber, accuracy);
        return update;
    }

    /**
     * Aggregate local updates using Federated Averaging (FedAvg)
     * Takes the weighted average of all local model weights
     *
     * @param localUpdates List of local updates from devices
     * @return Aggregated global weights as JSON string
     */
    public String aggregateUpdates(List<FederatedUpdate> localUpdates) {
        if (localUpdates == null || localUpdates.isEmpty()) return "[]";

        int numDevices = localUpdates.size();
        double[] globalWeights = new double[WEIGHT_COUNT];

        // Sum all local weights
        for (FederatedUpdate update : localUpdates) {
            double[] localWeights = parseWeights(update.getLocalWeights());
            for (int i = 0; i < WEIGHT_COUNT && i < localWeights.length; i++) {
                globalWeights[i] += localWeights[i];
            }
        }

        // Average (FedAvg)
        StringBuilder globalJson = new StringBuilder("[");
        for (int i = 0; i < WEIGHT_COUNT; i++) {
            globalWeights[i] = Math.round((globalWeights[i] / numDevices) * 10000.0) / 10000.0;
            if (i > 0) globalJson.append(",");
            globalJson.append(String.format("%.4f", globalWeights[i]));
        }
        globalJson.append("]");

        return globalJson.toString();
    }

    /**
     * Run a complete federated learning round
     * 1. Each device does local training
     * 2. Server aggregates weights
     * 3. Results are stored
     *
     * @return List of updates from this round
     */
    public List<FederatedUpdate> runRound() {
        int currentRound = federatedDAO.getLatestRound() + 1;
        List<FederatedUpdate> roundUpdates = new ArrayList<>();

        System.out.println("[FederatedLearning] Starting Round " + currentRound);

        // Step 1: Local training on each device
        for (String deviceId : DEVICE_IDS) {
            FederatedUpdate localUpdate = simulateLocalTraining(deviceId, currentRound);
            roundUpdates.add(localUpdate);
            System.out.printf("  [%s] Local training complete. Accuracy: %.2f%%%n",
                    deviceId, localUpdate.getAccuracy() * 100);
        }

        // Step 2: Aggregate updates (Federated Averaging)
        String globalWeights = aggregateUpdates(roundUpdates);
        System.out.println("  [Server] Aggregation complete. Global weights: " + globalWeights);

        // Step 3: Store updates with global weights
        for (FederatedUpdate update : roundUpdates) {
            update.setGlobalWeights(globalWeights);
            federatedDAO.saveUpdate(update);
        }

        System.out.println("[FederatedLearning] Round " + currentRound + " complete.");
        return roundUpdates;
    }

    /**
     * Parse weights JSON string to double array
     */
    public static double[] parseWeights(String weightsJson) {
        if (weightsJson == null || weightsJson.isEmpty()) return new double[0];

        // Remove brackets and split
        String cleaned = weightsJson.replace("[", "").replace("]", "").trim();
        if (cleaned.isEmpty()) return new double[0];

        String[] parts = cleaned.split(",");
        double[] weights = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                weights[i] = Double.parseDouble(parts[i].trim());
            } catch (NumberFormatException e) {
                weights[i] = 0;
            }
        }
        return weights;
    }

    /**
     * Compare local weights vs global weights
     * Returns a divergence score (lower = more aligned)
     */
    public static double calculateDivergence(String localWeightsJson, String globalWeightsJson) {
        double[] local = parseWeights(localWeightsJson);
        double[] global = parseWeights(globalWeightsJson);
        if (local.length == 0 || global.length == 0) return 0;

        double sumSquaredDiff = 0;
        int length = Math.min(local.length, global.length);
        for (int i = 0; i < length; i++) {
            double diff = local[i] - global[i];
            sumSquaredDiff += diff * diff;
        }

        return Math.round(Math.sqrt(sumSquaredDiff / length) * 10000.0) / 10000.0;
    }

    /**
     * Get the latest round number
     */
    public int getLatestRound() {
        return federatedDAO.getLatestRound();
    }

    /**
     * Get all updates
     */
    public List<FederatedUpdate> getAllUpdates() {
        return federatedDAO.getAllUpdates();
    }

    /**
     * Get updates by round
     */
    public List<FederatedUpdate> getUpdatesByRound(int round) {
        return federatedDAO.getUpdatesByRound(round);
    }

    /**
     * Get device IDs
     */
    public static String[] getDeviceIds() {
        return DEVICE_IDS;
    }
}
