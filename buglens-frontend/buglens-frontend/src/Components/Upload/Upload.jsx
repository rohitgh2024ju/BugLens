import { UploadCloud, FileUp } from "lucide-react";
import "./Upload.css";
import { useRef, useState } from "react";

export function Upload() {
  const fileInputRef = useRef(null);
  const [error, setError] = useState("");
  const [isDragging, setIsDragging] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const MAX_FILE_SIZE = 10 * 1024 * 1024;

  const handleBrowse = () => {
    fileInputRef.current.click();
  };

  const handleDragOver = (event) => {
    event.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (event) => {
    event.preventDefault();
    setIsDragging(false);

    const file = event.dataTransfer.files[0];

    validateFile(file);
  };

  const validateFile = (file) => {
    if (!file) {
      return;
    }
    const fileName = file.name.toLowerCase();

    if (!fileName.endsWith(".log") && !fileName.endsWith(".txt")) {
      setError("Only .log and .txt files are supported");
      return;
    }

    if (file.size > MAX_FILE_SIZE) {
      setError("File size cannot exceed 10 MB.");
      return;
    }

    setError("");

    setSelectedFile(file);
    console.log("Valid file:", file);
  };

  const handleDiscard = () => {
    setSelectedFile(null);
    setError("");

    if (fileInputRef) {
      fileInputRef.current.value = "";
    }
  };

  const handleFileSelect = (event) => {
    const file = event.target.files[0];

    validateFile(file);
  };

  return (
    <div className="upload-page">
      <div className="upload-header">
        <h1>Upload Log File</h1>

        <p>
          BugLens will parse your logs, detect incidents, and build an event
          dependency graph automatically.
        </p>
      </div>
      <div
        className={`upload-box ${isDragging ? "dragging" : ""}`}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        {selectedFile ? (
          <div className="selected-file">
            <div className="selected-file-icon">
              <FileUp size={28} />
            </div>

            <h2>{selectedFile.name}</h2>
            <p>{(selectedFile.size / (1024 * 1024)).toFixed(2)} MB</p>

            <div className="file-actions">
              <button className="discard-button" onClick={handleDiscard}>
                Discard
              </button>

              <button className="analyze-button">
                Analyze
              </button>
            </div>
          </div>
        ) : (
          <>
            <div className="upload-icon">
              <UploadCloud size={36} />
            </div>
            <h2>Drag & drop your log file here</h2>

            <p className="upload-supported">
              Supports .jsonl, .log — up to 10MB
            </p>

            <input
              ref={fileInputRef}
              type="file"
              accept=".log,.txt"
              hidden
              onChange={handleFileSelect}
            />

            <button className="browse-button" onClick={handleBrowse}>
              <FileUp size={18} />
              <span>Browse files</span>
            </button>
          </>
        )}
      </div>

      {error && (
        <div className="upload-error">
          <span className="error-dot"></span>
          <span>ERROR</span>
          <span className="error-message">{error}</span>
        </div>
      )}
    </div>
  );
}
