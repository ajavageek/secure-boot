package ch.frankel.blog.secureboot

employees := data.hierarchy

default allow := false

# Allow users to get their own salaries.
allow {
	input.path == ["finance", "salary", input.user]
}

# Allow managers to get their subordinates' salaries.
allow {
	some username
	input.path = ["finance", "salary", username]
	employees[input.user][_] == username
}
