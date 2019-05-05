package util

// StringArray provides gomobile-compatible way of reading an array of strings from Java/ObjC
type StringArray struct {
	data []string
}

type StringArrayPair struct {
	Index int
	Value string
}

func NewStringArray(data []string) *StringArray {
	return &StringArray{data}
}

func (this *StringArray) Length() int {
	return len(this.data)
}

func (this *StringArray) Get(index int) string {
	if index >= len(this.data) {
		return ""
	}
	return this.data[index]
}
