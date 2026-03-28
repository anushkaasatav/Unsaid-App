const API_BASE = ""; // same origin (http://localhost:8080)
document.getElementById("apiBaseLabel").textContent = location.origin;

const loginCard = document.getElementById("loginCard");
const appCard = document.getElementById("appCard");

const userChip = document.getElementById("userChip");
const chipHandle = document.getElementById("chipHandle");

const handleInput = document.getElementById("handleInput");
const passInput = document.getElementById("passInput");
const loginBtn = document.getElementById("loginBtn");
const logoutBtn = document.getElementById("logoutBtn");

const refreshBtn = document.getElementById("refreshBtn");
const postContent = document.getElementById("postContent");
const postBtn = document.getElementById("postBtn");
const charCount = document.getElementById("charCount");
const feed = document.getElementById("feed");

function getHandle() {
  return localStorage.getItem("unsaid_handle") || "";
}
function setHandle(h) {
  localStorage.setItem("unsaid_handle", h);
}

function showApp() {
  const h = getHandle();
  chipHandle.textContent = "@" + h;
  userChip.hidden = false;
  loginCard.hidden = true;
  appCard.hidden = false;
}
function showLogin() {
  userChip.hidden = true;
  loginCard.hidden = false;
  appCard.hidden = true;
}

function fmtTime(iso) {
  try {
    const d = new Date(iso);
    return d.toLocaleString();
  } catch {
    return iso;
  }
}

async function apiGet(path) {
  const res = await fetch(API_BASE + path);
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}
async function apiPost(path, bodyObj) {
  const res = await fetch(API_BASE + path, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: bodyObj ? JSON.stringify(bodyObj) : null
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

// LOGIN
loginBtn.addEventListener("click", () => {
  const h = (handleInput.value || "").trim();
  const p = (passInput.value || "").trim();
  if (!h) return alert("Handle required");
  if (!p) return alert("Password required (demo: anything)");
  setHandle(h);
  showApp();
  loadFeed();
});

logoutBtn.addEventListener("click", () => {
  localStorage.removeItem("unsaid_handle");
  showLogin();
});

refreshBtn.addEventListener("click", loadFeed);

postContent.addEventListener("input", () => {
  charCount.textContent = `${postContent.value.length} / 1000`;
});

postBtn.addEventListener("click", async () => {
  const h = getHandle();
  const content = (postContent.value || "").trim();
  if (!content) return alert("Post content cannot be empty");
  postBtn.disabled = true;
  try {
    await apiPost("/posts", { content, authorHandle: h });
    postContent.value = "";
    charCount.textContent = "0 / 1000";
    await loadFeed();
  } catch (e) {
    alert("Post failed: " + e.message);
  } finally {
    postBtn.disabled = false;
  }
});

function postEl(p) {
  const div = document.createElement("div");
  div.className = "post";

  const head = document.createElement("div");
  head.className = "postHead";
  head.innerHTML = `
    <div>
      <div class="handle">@${escapeHtml(p.authorHandle)}</div>
      <div class="time">${fmtTime(p.createdAt)}</div>
    </div>
    <div class="time">#${p.id}</div>
  `;

  const body = document.createElement("div");
  body.className = "postContent";
  body.textContent = p.content;

  const actions = document.createElement("div");
  actions.className = "actions";

  const likeBtn = document.createElement("div");
  likeBtn.className = "badge";
  likeBtn.innerHTML = `❤️ <span>${p.likeCount ?? 0}</span> <small>Like</small>`;
  likeBtn.addEventListener("click", async () => {
    likeBtn.style.opacity = "0.6";
    try {
      const updated = await apiPost(`/posts/${p.id}/like`);
      // update count in UI
      likeBtn.querySelector("span").textContent = updated.likeCount ?? 0;
    } catch (e) {
      alert("Like failed: " + e.message);
    } finally {
      likeBtn.style.opacity = "1";
    }
  });

  const cBtn = document.createElement("div");
  cBtn.className = "badge";
  cBtn.innerHTML = `💬 <span>${p.commentCount ?? 0}</span> <small>Comments</small>`;

  const commentsWrap = document.createElement("div");
  commentsWrap.className = "comments";

  cBtn.addEventListener("click", async () => {
    const open = commentsWrap.style.display === "block";
    if (open) {
      commentsWrap.style.display = "none";
      return;
    }
    commentsWrap.style.display = "block";
    await loadComments(p.id, commentsWrap, cBtn);
  });

  actions.appendChild(likeBtn);
  actions.appendChild(cBtn);

  div.appendChild(head);
  div.appendChild(body);
  div.appendChild(actions);
  div.appendChild(commentsWrap);
  return div;
}

async function loadFeed() {
  feed.innerHTML = `<div class="muted">Loading...</div>`;
  try {
    const posts = await apiGet("/posts");
    if (!Array.isArray(posts) || posts.length === 0) {
      feed.innerHTML = `<div class="muted">No posts yet. Create the first one 👇</div>`;
      return;
    }
    feed.innerHTML = "";
    posts.forEach(p => feed.appendChild(postEl(p)));
  } catch (e) {
    feed.innerHTML = `<div class="muted">Failed to load posts: ${escapeHtml(e.message)}</div>`;
  }
}

async function loadComments(postId, wrap, badgeEl) {
  wrap.innerHTML = `<div class="muted">Loading comments...</div>`;
  try {
    const comments = await apiGet(`/posts/${postId}/comments`);
    wrap.innerHTML = "";

    const list = document.createElement("div");
    comments.forEach(c => {
      const item = document.createElement("div");
      item.className = "comment";
      item.innerHTML = `
        <div class="meta">
          <span>@${escapeHtml(c.authorHandle)}</span>
          <span>${fmtTime(c.createdAt)}</span>
        </div>
        <div>${escapeHtml(c.content)}</div>
      `;
      list.appendChild(item);
    });

    const box = document.createElement("div");
    box.className = "commentBox";

    const inp = document.createElement("input");
    inp.placeholder = "Write a comment...";
    inp.maxLength = 600;

    const btn = document.createElement("button");
    btn.className = "btn btn-ghost";
    btn.textContent = "Send";

    btn.addEventListener("click", async () => {
      const h = getHandle();
      const content = (inp.value || "").trim();
      if (!content) return alert("Comment cannot be empty");
      btn.disabled = true;
      try {
        await apiPost(`/posts/${postId}/comments`, { content, authorHandle: h });
        inp.value = "";
        // refresh comments and also bump badge count by re-fetching posts later
        await loadComments(postId, wrap, badgeEl);
        // quick update comment count in badge
        const newCount = Number(badgeEl.querySelector("span").textContent || "0") + 1;
        badgeEl.querySelector("span").textContent = String(newCount);
      } catch (e) {
        alert("Comment failed: " + e.message);
      } finally {
        btn.disabled = false;
      }
    });

    box.appendChild(inp);
    box.appendChild(btn);

    wrap.appendChild(list);
    wrap.appendChild(box);

  } catch (e) {
    wrap.innerHTML = `<div class="muted">Failed to load comments: ${escapeHtml(e.message)}</div>`;
  }
}

function escapeHtml(s){
  return String(s ?? "")
    .replaceAll("&","&amp;")
    .replaceAll("<","&lt;")
    .replaceAll(">","&gt;")
    .replaceAll('"',"&quot;")
    .replaceAll("'","&#039;");
}

// init
if (getHandle()) {
  showApp();
  loadFeed();
} else {
  showLogin();
}
