// passu_mobile implements a gobind-friendly wrapper for passu-lib, so it can be used from Java/ObjC
package passu_mobile

import (
	"errors"
	"github.com/guregu/null"
	"github.com/winded/passu-lib"
	"github.com/winded/passu-mobile/passu_mobile/util"
	"sort"
)

type IStream interface {
	GetLength() int
	Close() error
}

type IReader interface {
	IStream
	Read() ([]byte, error)
}

type IWriter interface {
	IStream
	Write(data []byte) error
}

type IFileHandle interface {
	IsReadOnly() bool
	GetPath() string

	OpenReader() (IReader, error)
	OpenWriter() (IWriter, error)
}

type PasswordDatabase struct {
	db         *passulib.PasswordDatabase
	fileHandle IFileHandle
}

func PasswordDatabaseFromFile(fileHandle IFileHandle, password string) (*PasswordDatabase, error) {
	r, err := fileHandle.OpenReader()
	if err != nil {
		return nil, err
	}
	defer r.Close()

	bytes, err := r.Read()
	if err != nil {
		return nil, err
	}

	db, err := passulib.PasswordDatabaseFromData(bytes, password)
	if err != nil {
		return nil, err
	}

	dbHandle := &PasswordDatabase{
		db:         db,
		fileHandle: fileHandle,
	}

	return dbHandle, nil
}

func NewPasswordDatabase(fileHandle IFileHandle, password string) *PasswordDatabase {
	return &PasswordDatabase{
		db:         passulib.NewPasswordDatabase(password),
		fileHandle: fileHandle,
	}
}

func (this *PasswordDatabase) IsModified() bool {
	return this.db.Modified
}

func (this *PasswordDatabase) GetFileHandle() IFileHandle {
	return this.fileHandle
}

func (this *PasswordDatabase) GetDefaultPolicy() *PasswordPolicy {
	return &PasswordPolicy{this.db.GetDefaultPolicy()}
}

func (this *PasswordDatabase) SetDefaultPolicy(updatedPolicy *PasswordPolicy) error {
	return this.db.SetDefaultPolicy(updatedPolicy.p)
}

func (this *PasswordDatabase) GetEntryNames(filter string) *util.StringArray {
	var entries []passulib.PasswordEntry
	if filter != "" {
		entries = this.db.FindEntries(filter)
	} else {
		entries = this.db.AllEntries()
	}

	names := make([]string, len(entries))
	for idx, entry := range entries {
		names[idx] = entry.Name
	}
	sort.Strings(names)

	return util.NewStringArray(names)
}

func (this *PasswordDatabase) GetEntry(name string) (*PasswordEntry, error) {
	entry, idx := this.db.GetEntry(name)
	if idx == -1 {
		return nil, errors.New("Entry not found")
	}
	return &PasswordEntry{entry}, nil
}

func (this *PasswordDatabase) AddEntry(entry *PasswordEntry) error {
	return this.db.AddEntry(entry.e)
}

func (this *PasswordDatabase) UpdateEntry(name string, updatedEntry *PasswordEntry) error {
	return this.db.UpdateEntry(name, updatedEntry.e)
}

func (this *PasswordDatabase) RemoveEntry(name string) error {
	_, err := this.db.RemoveEntry(name)
	return err
}

func (this *PasswordDatabase) GeneratePassword(entryName string) (*PasswordEntry, error) {
	entry, err := this.db.GeneratePassword(entryName)
	if err == nil {
		return &PasswordEntry{entry}, nil
	} else {
		return nil, err
	}
}

func (this *PasswordDatabase) Save() error {
	bytes := this.db.Save()

	w, err := this.fileHandle.OpenWriter()
	if err != nil {
		return err
	}
	defer w.Close()

	err = w.Write(bytes)
	if err != nil {
		return err
	}

	return nil
}

type PasswordEntry struct {
	e passulib.PasswordEntry
}

func NewPasswordEntry() *PasswordEntry {
	return &PasswordEntry{}
}

func (this *PasswordEntry) GetName() string {
	return this.e.Name
}
func (this *PasswordEntry) SetName(name string) {
	this.e.Name = name
}

func (this *PasswordEntry) GetPassword() string {
	return this.e.Password
}
func (this *PasswordEntry) SetPassword(password string) {
	this.e.Password = password
}

func (this *PasswordEntry) GetDescription() string {
	return this.e.Description
}
func (this *PasswordEntry) SetDescription(description string) {
	this.e.Description = description
}

func (this *PasswordEntry) GetPolicy() *PasswordPolicy {
	return &PasswordPolicy{this.e.PolicyOverride}
}
func (this *PasswordEntry) SetPolicy(policy *PasswordPolicy) {
	this.e.PolicyOverride = policy.p
}

type PasswordPolicy struct {
	p passulib.PasswordPolicy
}

func NewPasswordPolicy() *PasswordPolicy {
	return &PasswordPolicy{}
}

func (this *PasswordPolicy) HasLength() bool {
	return this.p.Length.Valid
}
func (this *PasswordPolicy) ResetLength() {
	this.p.Length = null.NewInt(0, false)
}
func (this *PasswordPolicy) GetLength() int {
	return int(this.p.Length.ValueOrZero())
}
func (this *PasswordPolicy) SetLength(value int) {
	this.p.Length = null.IntFrom(int64(value))
}

func (this *PasswordPolicy) HasUseLowercase() bool {
	return this.p.UseLowercase.Valid
}
func (this *PasswordPolicy) ResetUseLowercase() {
	this.p.UseLowercase = null.NewBool(false, false)
}
func (this *PasswordPolicy) GetUseLowercase() bool {
	return this.p.UseLowercase.ValueOrZero()
}
func (this *PasswordPolicy) SetUseLowercase(value bool) {
	this.p.UseLowercase = null.BoolFrom(value)
}

func (this *PasswordPolicy) HasUseUppercase() bool {
	return this.p.UseUppercase.Valid
}
func (this *PasswordPolicy) ResetUseUppercase() {
	this.p.UseUppercase = null.NewBool(false, false)
}
func (this *PasswordPolicy) GetUseUppercase() bool {
	return this.p.UseUppercase.ValueOrZero()
}
func (this *PasswordPolicy) SetUseUppercase(value bool) {
	this.p.UseUppercase = null.BoolFrom(value)
}

func (this *PasswordPolicy) HasUseNumbers() bool {
	return this.p.UseNumbers.Valid
}
func (this *PasswordPolicy) ResetUseNumbers() {
	this.p.UseNumbers = null.NewBool(false, false)
}
func (this *PasswordPolicy) GetUseNumbers() bool {
	return this.p.UseNumbers.ValueOrZero()
}
func (this *PasswordPolicy) SetUseNumbers(value bool) {
	this.p.UseNumbers = null.BoolFrom(value)
}

func (this *PasswordPolicy) HasUseSpecial() bool {
	return this.p.UseSpecial.Valid
}
func (this *PasswordPolicy) ResetUseSpecial() {
	this.p.UseSpecial = null.NewBool(false, false)
}
func (this *PasswordPolicy) GetUseSpecial() bool {
	return this.p.UseSpecial.ValueOrZero()
}
func (this *PasswordPolicy) SetUseSpecial(value bool) {
	this.p.UseSpecial = null.BoolFrom(value)
}
