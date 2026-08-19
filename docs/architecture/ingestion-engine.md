Ingestion Engine

Responsibility:
Identify the structural pattern of log data.

Initial Design Constraint:
For BugLens v1, one file is assumed to contain
one consistent log structure.

file_id → detected structure

Example:

file-5310
    ↓
{TIMESTAMP} {LEVEL} [{THREAD}] {LOGGER} - {MESSAGE}

The structure is detected once per file and is
then used by the Parser to parse all records
associated with that file_id.

Future consideration:
Support multiple structures within a single file.