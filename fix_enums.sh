#!/bin/bash

# Add status getter/setter to ContactMessage
sed -i '' '/public enum Status {/i\
\
    public Status getStatus() {\
        return status;\
    }\
\
    public void setStatus(Status status) {\
        this.status = status;\
    }
' src/main/java/com/qpmanagement/entity/ContactMessage.java

# Add status getter/setter to QuestionPaper
sed -i '' '/public enum Status {/i\
\
    public Status getStatus() {\
        return status;\
    }\
\
    public void setStatus(Status status) {\
        this.status = status;\
    }\
\
    public User getVerifiedBy() {\
        return verifiedBy;\
    }\
\
    public void setVerifiedBy(User verifiedBy) {\
        this.verifiedBy = verifiedBy;\
    }\
\
    public LocalDateTime getVerifiedAt() {\
        return verifiedAt;\
    }\
\
    public void setVerifiedAt(LocalDateTime verifiedAt) {\
        this.verifiedAt = verifiedAt;\
    }
' src/main/java/com/qpmanagement/entity/QuestionPaper.java

# Add status getter/setter to Project
sed -i '' '/public enum Status {/i\
\
    public Status getStatus() {\
        return status;\
    }\
\
    public void setStatus(Status status) {\
        this.status = status;\
    }
' src/main/java/com/qpmanagement/entity/Project.java

# Add platform getter/setter to Solution
sed -i '' '/public enum Platform {/i\
\
    public Platform getPlatform() {\
        return platform;\
    }\
\
    public void setPlatform(Platform platform) {\
        this.platform = platform;\
    }
' src/main/java/com/qpmanagement/entity/Solution.java

echo "Enum getters/setters added!"
