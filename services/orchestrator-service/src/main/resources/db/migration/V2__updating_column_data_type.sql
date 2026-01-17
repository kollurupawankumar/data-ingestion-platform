-- Step 1: Add new BIGSERIAL column
ALTER TABLE job_execution
ADD COLUMN dataset_id_new BIGSERIAL NOT NULL;

-- Step 2: Update any child tables that reference this (if needed)
-- (Skip if no foreign keys)

-- Step 3: Drop the old UUID column
ALTER TABLE job_execution
DROP COLUMN dataset_id;

-- Step 4: Rename the new column
ALTER TABLE job_execution
RENAME COLUMN dataset_id_new TO dataset_id;