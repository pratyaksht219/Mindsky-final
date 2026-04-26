Build a modern, clean, and visually appealing **DevOps Monitoring Dashboard UI** for a Kubernetes-based microservices system called "MentalHealthPipeline".

### 🎯 Objective:

Create a real-time observability dashboard that visualizes system health, performance, and reliability using metrics from Prometheus and Grafana.

---

### 🧱 Tech Requirements:

* Frontend: React (with Tailwind CSS)
* Charts: Recharts or Chart.js
* UI Style: Minimal, modern, dark theme (DevOps-style)
* Responsive design (desktop-first)

---

### 📊 Dashboard Sections:

#### 1. 🟢 System Overview

* Total number of services
* Number of running pods
* Number of unhealthy pods
* Total requests per second
* System uptime %

---

#### 2. 📈 Service-Level Metrics (per microservice)

For each service (User Service, Processing Service, Notification Service, etc.):

* Requests per second (line chart)
* Average latency (ms)
* Error rate (%)
* Status indicator (Healthy / Warning / Down)

---

#### 3. ⚙️ Resource Usage (Kubernetes)

* CPU usage per pod (line or bar chart)
* Memory usage per pod
* Pod restart count
* Node-level metrics (optional)

---

#### 4. 🚨 Alerts & Failures Panel

* Show recent alerts:

  * High CPU usage
  * Pod crash (OOMKilled)
  * Service downtime
* Use color coding:

  * Red → Critical
  * Yellow → Warning
  * Green → Healthy

---

#### 5. 📡 Traffic Simulation Panel (Demo Feature)

* Button to simulate:

  * High traffic
  * CPU spike
  * Memory leak
* Reflect changes in charts dynamically (mock data is fine)

---

### 🎨 UI/UX Design:

* Dark theme (black/gray background)
* Neon/bright accent colors (green, blue, red)
* Card-based layout
* Smooth animations (hover, transitions)
* Clean typography

---

### 🧠 Behavior:

* Use mock Prometheus-like JSON data for now
* Structure code so real API integration can be plugged in later
* Organize components cleanly:

  * Dashboard
  * ServiceCard
  * MetricsChart
  * AlertsPanel

---

### 📦 Bonus (if possible):

* Add auto-refresh every 5 seconds
* Add time range selector (last 5 min, 15 min, 1 hour)
* Add filter by service

---

### 🎤 End Goal:

The dashboard should look like a real production-grade DevOps monitoring system that can be used in a presentation to demonstrate:

* Kubernetes observability
* Microservices monitoring
* System health tracking
* Failure detection and analysis

Make it visually impressive and easy to explain.
