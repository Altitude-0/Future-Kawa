import uuid
import random
from datetime import datetime, timedelta

def random_date(start, end):
    return start + timedelta(seconds=random.randint(0, int((end - start).total_seconds())))

now = datetime.now()
six_months_ago = now - timedelta(days=180)
two_months_ago = now - timedelta(days=60)

countries = [
    {'id': 'c1111111-89ab-cdef-0123-456789abcdef', 'code': 'BR', 'name': 'Brazil', 'temp': 29.0, 'hum': 55.0, 'wh_id': '11111111-1111-1111-1111-111111111111', 'wh_name': 'Sao Paulo Warehouse'},
    {'id': 'c2222222-89ab-cdef-0123-456789abcdef', 'code': 'CO', 'name': 'Colombia', 'temp': 26.0, 'hum': 80.0, 'wh_id': '22222222-2222-2222-2222-222222222222', 'wh_name': 'Medellin Warehouse'},
    {'id': 'c3333333-89ab-cdef-0123-456789abcdef', 'code': 'EC', 'name': 'Ecuador', 'temp': 31.0, 'hum': 60.0, 'wh_id': '33333333-3333-3333-3333-333333333333', 'wh_name': 'Quito Warehouse'}
]

sensor_type_id = 'f1111111-89ab-cdef-0123-456789abcdef'

sql = []
sql.append("-- =====================================================")
sql.append("-- FutureKawa - Auto-Generated Seed Data Script")
sql.append("-- =====================================================\n")

sql.append("TRUNCATE TABLE country CASCADE;")
sql.append("TRUNCATE TABLE warehouse CASCADE;")
sql.append("TRUNCATE TABLE sensors_type CASCADE;\n")

sql.append(f"INSERT INTO sensors_type (id, sensor_type) VALUES ('{sensor_type_id}', 'DHT11');\n")

sql.append("INSERT INTO country (id, code_iso, name) VALUES")
sql.append(",\n".join([f"('{c['id']}', '{c['code']}', '{c['name']}')" for c in countries]) + ";\n")

sql.append("INSERT INTO configurations (id, fk_country, temperature_ideal, temperature_tolerance, humidity_ideal, humidity_tolerance, temperature_unit, created_at) VALUES")
configs = []
for c in countries:
    configs.append(f"(gen_random_uuid(), '{c['id']}', {c['temp']}, 3.0, {c['hum']}, 2.0, 'CELSIUS', CURRENT_TIMESTAMP)")
sql.append(",\n".join(configs) + ";\n")

sql.append("INSERT INTO warehouse (id, name, fk_country, ideal_temperature, tolerance_temperature) VALUES")
whs = []
for c in countries:
    whs.append(f"('{c['wh_id']}', '{c['wh_name']}', '{c['id']}', {c['temp']}, 3.0)")
sql.append(",\n".join(whs) + ";\n")

sensors = []
containers = []
measurements = []
alerts = []

for i in range(100):
    c = random.choice(countries)
    sensor_id = str(uuid.uuid4())
    container_id = str(uuid.uuid4())
    
    entry_date = random_date(six_months_ago, now)
    exit_date = "NULL"
    status = "COMPLIANT"
    
    # Simulate exit for some containers (if older than 3 months)
    if entry_date < now - timedelta(days=90) and random.random() > 0.5:
        exit_date_dt = random_date(entry_date + timedelta(days=10), now)
        exit_date = f"'{exit_date_dt.strftime('%Y-%m-%d %H:%M:%S')}'"
    else:
        exit_date_dt = now
    
    # Mark as outdated if artificially generated
    if random.random() < 0.05:
        entry_date = now - timedelta(days=400)
        status = "OUTDATED"
        exit_date_dt = now
    
    sensors.append(f"('{sensor_id}', '{sensor_type_id}', '{entry_date.strftime('%Y-%m-%d %H:%M:%S')}', '{c['code']}-SENS-{i:03d}')")
    
    has_alert = False
    
    # Generate 50 measurements evenly distributed between entry and exit
    step = (exit_date_dt - entry_date) / 50
    if step.total_seconds() <= 0:
        step = timedelta(hours=1)
        
    for j in range(50):
        m_date = entry_date + step * j
        t = round(random.gauss(c['temp'], 1.5), 1)
        h = round(random.gauss(c['hum'], 2.5), 1)
        
        if abs(t - c['temp']) > 3.0:
            has_alert = True
            status = "WARNING" if status != "OUTDATED" else status
            alerts.append(f"(gen_random_uuid(), '{container_id}', 'TEMPERATURE_OUT_OF_RANGE', '{m_date.strftime('%Y-%m-%d %H:%M:%S')}', false, 'Température anormale: {t}°C', '{m_date.strftime('%Y-%m-%d %H:%M:%S')}')")
            
        measurements.append(f"(gen_random_uuid(), '{sensor_id}', {t}, {h}, '{m_date.strftime('%Y-%m-%d %H:%M:%S')}')")
        
    containers.append(f"('{container_id}', '{c['wh_id']}', '{entry_date.strftime('%Y-%m-%d %H:%M:%S')}', {exit_date}, '{sensor_id}', '{status}', 'CONT-{c['code']}-{i:03d}')")

sql.append("INSERT INTO sensors (id, fk_sensor_type, entry_date, reference) VALUES")
def chunk(lst, n):
    for i in range(0, len(lst), n):
        yield lst[i:i + n]

for c_sensors in chunk(sensors, 100):
    sql.append(",\n".join(c_sensors) + ";\n")

sql.append("INSERT INTO containers (id, fk_warehouse, entry_date, exit_date, id_sensor, status, reference) VALUES")
for c_containers in chunk(containers, 100):
    sql.append(",\n".join(c_containers) + ";\n")

sql.append("INSERT INTO measurements (id, fk_sensors, temperature, humidity, created_at) VALUES")
all_meas = list(chunk(measurements, 500))
for idx, c_meas in enumerate(all_meas):
    sql.append(",\n".join(c_meas) + ";\n")
    if idx < len(all_meas) - 1:
        sql.append("INSERT INTO measurements (id, fk_sensors, temperature, humidity, created_at) VALUES")

if alerts:
    sql.append("INSERT INTO alerts (id, fk_containers, type, alerted_at, email_sent, description, created_at) VALUES")
    all_alerts = list(chunk(alerts, 100))
    for idx, c_alerts in enumerate(all_alerts):
        sql.append(",\n".join(c_alerts) + ";\n")
        if idx < len(all_alerts) - 1:
            sql.append("INSERT INTO alerts (id, fk_containers, type, alerted_at, email_sent, description, created_at) VALUES")

with open('/Users/mohameddjebali/Desktop/mspr-2/futurekawa-backend-core/src/main/resources/db/scripts/seed_data.sql', 'w') as f:
    f.write("\n".join(sql))

print("Seed data generated successfully.")
