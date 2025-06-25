-- Insert developers from existing assignedTo values
INSERT INTO developers (id, name, email_address)
SELECT nextval('developer_id_seq'), assigned_to, null
FROM (SELECT DISTINCT assigned_to FROM features WHERE assigned_to IS NOT NULL) AS distinct_developers;

-- Add developer_id column to features table
ALTER TABLE features ADD COLUMN developer_id bigint;

-- Update developer_id in features based on assigned_to values
UPDATE features f
SET developer_id = d.id
    FROM developers d
WHERE f.assigned_to = d.name;

-- Add foreign key constraint
ALTER TABLE features
    ADD CONSTRAINT fk_features_developer_id FOREIGN KEY (developer_id) REFERENCES developers (id);