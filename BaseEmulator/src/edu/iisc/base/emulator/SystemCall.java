package edu.iisc.base.emulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemCall {
	
	public Map<String, Integer> sTrace = new HashMap<String, Integer>();

	public static String[] mips_syscall_names = { "sys_syscall", "sys_exit",
			"sys_fork", "sys_read", "sys_write", "sys_open", "sys_close",
			"sys_waitpid", "sys_creat", "sys_link", "sys_unlink", "sys_execve",
			"sys_chdir", "sys_time", "sys_mknod", "sys_chmod", "sys_lchown",
			"sys_ni_syscall", "sys_ni_syscall", "sys_lseek", "sys_getpid",
			"sys_mount", "sys_oldumount", "sys_setuid", "sys_getuid",
			"sys_stime", "sys_ptrace", "sys_alarm", "sys_ni_syscall",
			"sys_pause", "sys_utime", "sys_ni_syscall", "sys_ni_syscall",
			"sys_access", "sys_nice", "sys_ni_syscall", "sys_sync", "sys_kill",
			"sys_rename", "sys_mkdir", "sys_rmdir", "sys_dup", "sys_pipe",
			"sys_times", "sys_ni_syscall", "sys_brk", "sys_setgid",
			"sys_getgid", "sys_ni_syscall", "sys_geteuid", "sys_getegid",
			"sys_acct", "sys_umount", "sys_ni_syscall", "sys_ioctl",
			"sys_fcntl", "sys_ni_syscall", "sys_setpgid", "sys_ni_syscall",
			"sys_olduname", "sys_umask", "sys_chroot", "sys_ustat", "sys_dup2",
			"sys_getppid", "sys_getpgrp", "sys_setsid", "sys_sigaction",
			"sys_sgetmask", "sys_ssetmask", "sys_setreuid", "sys_setregid",
			"sys_sigsuspend", "sys_sigpending", "sys_sethostname",
			"sys_setrlimit", "sys_getrlimit", "sys_getrusage",
			"sys_gettimeofday", "sys_settimeofday", "sys_getgroups",
			"sys_setgroups", "sys_ni_syscall", "sys_symlink", "sys_ni_syscall",
			"sys_readlink", "sys_uselib", "sys_swapon", "sys_reboot",
			"old_readdir", "old_mmap", "sys_munmap", "sys_truncate",
			"sys_ftruncate", "sys_fchmod", "sys_fchown", "sys_getpriority",
			"sys_setpriority", "sys_ni_syscall", "sys_statfs", "sys_fstatfs",
			"sys_ni_syscall", "sys_socketcall", "sys_syslog", "sys_setitimer",
			"sys_getitimer", "sys_newstat", "sys_newlstat", "sys_newfstat",
			"sys_uname", "sys_ni_syscall", "sys_vhangup", "sys_ni_syscall",
			"sys_ni_syscall", "sys_wait4", "sys_swapoff", "sys_sysinfo",
			"sys_ipc", "sys_fsync", "sys_sigreturn", "sys_clone",
			"sys_setdomainname", "sys_newuname", "sys_ni_syscall",
			"sys_adjtimex", "sys_mprotect", "sys_sigprocmask",
			"sys_ni_syscall", "sys_init_module", "sys_delete_module",
			"sys_ni_syscall", "sys_quotactl", "sys_getpgid", "sys_fchdir",
			"sys_bdflush", "sys_sysfs", "sys_personality", "sys_ni_syscall",
			"sys_setfsuid", "sys_setfsgid", "sys_llseek", "sys_getdents",
			"sys_select", "sys_flock", "sys_msync", "sys_readv", "sys_writev",
			"sys_cacheflush", "sys_cachectl", "sys_sysmips", "sys_ni_syscall",
			"sys_getsid", "sys_fdatasync", "sys_sysctl", "sys_mlock",
			"sys_munlock", "sys_mlockall", "sys_munlockall",
			"sys_sched_setparam", "sys_sched_getparam",
			"sys_sched_setscheduler", "sys_sched_getscheduler",
			"sys_sched_yield", "sys_sched_get_priority_max",
			"sys_sched_get_priority_min", "sys_sched_rr_get_interval",
			"sys_nanosleep", "sys_mremap", "sys_accept", "sys_bind",
			"sys_connect", "sys_getpeername", "sys_getsockname",
			"sys_getsockopt", "sys_listen", "sys_recv", "sys_recvfrom",
			"sys_recvmsg", "sys_send", "sys_sendmsg", "sys_sendto",
			"sys_setsockopt", "sys_shutdown", "sys_socket", "sys_socketpair",
			"sys_setresuid", "sys_getresuid", "sys_ni_syscall", "sys_poll",
			"sys_nfsservctl", "sys_setresgid", "sys_getresgid", "sys_prctl",
			"sys_rt_sigreturn", "sys_rt_sigaction", "sys_rt_sigprocmask",
			"sys_rt_sigpending", "sys_rt_sigtimedwait", "sys_rt_sigqueueinfo",
			"sys_rt_sigsuspend", "sys_pread64", "sys_pwrite64", "sys_chown",
			"sys_getcwd", "sys_capget", "sys_capset", "sys_sigaltstack",
			"sys_sendfile", "sys_ni_syscall", "sys_ni_syscall", "sys_mmap2",
			"sys_truncate64", "sys_ftruncate64", "sys_stat64", "sys_lstat64",
			"sys_fstat64", "sys_pivot_root", "sys_mincore", "sys_madvise",
			"sys_getdents64", "sys_fcntl64", "sys_ni_syscall", "sys_gettid",
			"sys_readahead", "sys_setxattr", "sys_lsetxattr", "sys_fsetxattr",
			"sys_getxattr", "sys_lgetxattr", "sys_fgetxattr", "sys_listxattr",
			"sys_llistxattr", "sys_flistxattr", "sys_removexattr",
			"sys_lremovexattr", "sys_fremovexattr", "sys_tkill",
			"sys_sendfile64", "sys_futex", "sys_sched_setaffinity",
			"sys_sched_getaffinity", "sys_io_setup", "sys_io_destroy",
			"sys_io_getevents", "sys_io_submit", "sys_io_cancel",
			"sys_exit_group", "sys_lookup_dcookie", "sys_epoll_create",
			"sys_epoll_ctl", "sys_epoll_wait", "sys_remap_file_pages",
			"sys_set_tid_address", "sys_restart_syscall", "sys_fadvise64_64",
			"sys_statfs64", "sys_fstatfs64", "sys_timer_create",
			"sys_timer_settime", "sys_timer_gettime", "sys_timer_getoverrun",
			"sys_timer_delete", "sys_clock_settime", "sys_clock_gettime",
			"sys_clock_getres", "sys_clock_nanosleep", "sys_tgkill",
			"sys_utimes", "sys_mbind", "sys_ni_syscall", "sys_ni_syscall",
			"sys_mq_open", "sys_mq_unlink", "sys_mq_timedsend",
			"sys_mq_timedreceive", "sys_mq_notify", "sys_mq_getsetattr",
			"sys_ni_syscall", "sys_waitid", "sys_ni_syscall", "sys_add_key",
			"sys_request_key", "sys_keyctl", "sys_set_thread_area",
			"sys_inotify_init", "sys_inotify_add_watch",
			"sys_inotify_rm_watch", "sys_migrate_pages", "sys_openat",
			"sys_mkdirat", "sys_mknodat", "sys_fchownat", "sys_futimesat",
			"sys_fstatat64", "sys_unlinkat", "sys_renameat", "sys_linkat",
			"sys_symlinkat", "sys_readlinkat", "sys_fchmodat", "sys_faccessat",
			"sys_pselect6", "sys_ppoll", "sys_unshare", "sys_splice",
			"sys_sync_file_range", "sys_tee", "sys_vmsplice", "sys_move_pages",
			"sys_set_robust_list", "sys_get_robust_list", "sys_kexec_load",
			"sys_getcpu", "sys_epoll_pwait", "sys_ioprio_set",
			"sys_ioprio_get", "sys_utimensat", "sys_signalfd",
			"sys_ni_syscall", "sys_eventfd", "sys_fallocate",
			"sys_timerfd_create", "sys_timerfd_gettime", "sys_timerfd_settime",
			"sys_signalfd4", "sys_eventfd2", "sys_epoll_create1", "sys_dup3",
			"sys_pipe2", "sys_inotify_init1", "sys_preadv", "sys_pwritev",
			"sys_rt_tgsigqueueinfo", "sys_perf_event_open", "sys_accept4",
			"sys_recvmmsg", "sys_fanotify_init", "sys_fanotify_mark",
			"sys_prlimit64", "sys_name_to_handle_at", "sys_open_by_handle_at",
			"sys_clock_adjtime", "sys_syncfs" };

	public static int mips_syscall_args[] = { 
			8, // MIPS_SYS(sys_syscall , 8) /* 4000 */
			1, // MIPS_SYS(sys_exit , 1)
			0, // MIPS_SYS(sys_fork , 0)
			3, // MIPS_SYS(sys_read , 3)
			3, // MIPS_SYS(sys_write , 3)
			3, // MIPS_SYS(sys_open , 3) /* 4005 */
			1, // MIPS_SYS(sys_close , 1)
			3, // MIPS_SYS(sys_waitpid , 3)
			2, // MIPS_SYS(sys_creat , 2)
			2, // MIPS_SYS(sys_link , 2)
			1, // MIPS_SYS(sys_unlink , 1) /* 4010 */
			0, // MIPS_SYS(sys_execve , 0)
			1, // MIPS_SYS(sys_chdir , 1)
			1, // MIPS_SYS(sys_time , 1)
			3, // MIPS_SYS(sys_mknod , 3)
			2, // MIPS_SYS(sys_chmod , 2) /* 4015 */
			3, // MIPS_SYS(sys_lchown , 3)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was sys_stat */
			3, // MIPS_SYS(sys_lseek , 3)
			0, // MIPS_SYS(sys_getpid , 0) /* 4020 */
			5, // MIPS_SYS(sys_mount , 5)
			1, // MIPS_SYS(sys_oldumount , 1)
			1, // MIPS_SYS(sys_setuid , 1)
			0, // MIPS_SYS(sys_getuid , 0)
			1, // MIPS_SYS(sys_stime , 1) /* 4025 */
			4, // MIPS_SYS(sys_ptrace , 4)
			1, // MIPS_SYS(sys_alarm , 1)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was sys_fstat */
			0, // MIPS_SYS(sys_pause , 0)
			2, // MIPS_SYS(sys_utime , 2) /* 4030 */
			0, // MIPS_SYS(sys_ni_syscall , 0)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			2, // MIPS_SYS(sys_access , 2)
			1, // MIPS_SYS(sys_nice , 1)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* 4035 */
			0, // MIPS_SYS(sys_sync , 0)
			2, // MIPS_SYS(sys_kill , 2)
			2, // MIPS_SYS(sys_rename , 2)
			2, // MIPS_SYS(sys_mkdir , 2)
			1, // MIPS_SYS(sys_rmdir , 1) /* 4040 */
			1, // MIPS_SYS(sys_dup , 1)
			0, // MIPS_SYS(sys_pipe , 0)
			1, // MIPS_SYS(sys_times , 1)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			1, // MIPS_SYS(sys_brk , 1) /* 4045 */
			1, // MIPS_SYS(sys_setgid , 1)
			0, // MIPS_SYS(sys_getgid , 0)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was signal(2) */
			0, // MIPS_SYS(sys_geteuid , 0)
			0, // MIPS_SYS(sys_getegid , 0) /* 4050 */
			0, // MIPS_SYS(sys_acct , 0)
			2, // MIPS_SYS(sys_umount , 2)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			3, // MIPS_SYS(sys_ioctl , 3)
			3, // MIPS_SYS(sys_fcntl , 3) /* 4055 */
			2, // MIPS_SYS(sys_ni_syscall , 2)
			2, // MIPS_SYS(sys_setpgid , 2)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			1, // MIPS_SYS(sys_olduname , 1)
			1, // MIPS_SYS(sys_umask , 1) /* 4060 */
			1, // MIPS_SYS(sys_chroot , 1)
			2, // MIPS_SYS(sys_ustat , 2)
			2, // MIPS_SYS(sys_dup2 , 2)
			0, // MIPS_SYS(sys_getppid , 0)
			0, // MIPS_SYS(sys_getpgrp , 0) /* 4065 */
			0, // MIPS_SYS(sys_setsid , 0)
			3, // MIPS_SYS(sys_sigaction , 3)
			0, // MIPS_SYS(sys_sgetmask , 0)
			1, // MIPS_SYS(sys_ssetmask , 1)
			2, // MIPS_SYS(sys_setreuid , 2) /* 4070 */
			2, // MIPS_SYS(sys_setregid , 2)
			0, // MIPS_SYS(sys_sigsuspend , 0)
			1, // MIPS_SYS(sys_sigpending , 1)
			2, // MIPS_SYS(sys_sethostname , 2)
			2, // MIPS_SYS(sys_setrlimit , 2) /* 4075 */
			2, // MIPS_SYS(sys_getrlimit , 2)
			2, // MIPS_SYS(sys_getrusage , 2)
			2, // MIPS_SYS(sys_gettimeofday, 2)
			2, // MIPS_SYS(sys_settimeofday, 2)
			2, // MIPS_SYS(sys_getgroups , 2) /* 4080 */
			2, // MIPS_SYS(sys_setgroups , 2)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* old_select */
			2, // MIPS_SYS(sys_symlink , 2)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was sys_lstat */
			3, // MIPS_SYS(sys_readlink , 3) /* 4085 */
			1, // MIPS_SYS(sys_uselib , 1)
			2, // MIPS_SYS(sys_swapon , 2)
			3, // MIPS_SYS(sys_reboot , 3)
			3, // MIPS_SYS(old_readdir , 3)
			6, // MIPS_SYS(old_mmap , 6) /* 4090 */
			2, // MIPS_SYS(sys_munmap , 2)
			2, // MIPS_SYS(sys_truncate , 2)
			2, // MIPS_SYS(sys_ftruncate , 2)
			2, // MIPS_SYS(sys_fchmod , 2)
			3, // MIPS_SYS(sys_fchown , 3) /* 4095 */
			2, // MIPS_SYS(sys_getpriority , 2)
			3, // MIPS_SYS(sys_setpriority , 3)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			2, // MIPS_SYS(sys_statfs , 2)
			2, // MIPS_SYS(sys_fstatfs , 2) /* 4100 */
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was ioperm(2) */
			2, // MIPS_SYS(sys_socketcall , 2)
			3, // MIPS_SYS(sys_syslog , 3)
			3, // MIPS_SYS(sys_setitimer , 3)
			2, // MIPS_SYS(sys_getitimer , 2) /* 4105 */
			2, // MIPS_SYS(sys_newstat , 2)
			2, // MIPS_SYS(sys_newlstat , 2)
			2, // MIPS_SYS(sys_newfstat , 2)
			1, // MIPS_SYS(sys_uname , 1)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* 4110 was iopl(2) */
			0, // MIPS_SYS(sys_vhangup , 0)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was sys_idle() */
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was sys_vm86 */
			4, // MIPS_SYS(sys_wait4 , 4)
			1, // MIPS_SYS(sys_swapoff , 1) /* 4115 */
			1, // MIPS_SYS(sys_sysinfo , 1)
			6, // MIPS_SYS(sys_ipc , 6)
			1, // MIPS_SYS(sys_fsync , 1)
			0, // MIPS_SYS(sys_sigreturn , 0)
			6, // MIPS_SYS(sys_clone , 6) /* 4120 */
			2, // MIPS_SYS(sys_setdomainname, 2)
			1, // MIPS_SYS(sys_newuname , 1)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* sys_modify_ldt */
			1, // MIPS_SYS(sys_adjtimex , 1)
			3, // MIPS_SYS(sys_mprotect , 3) /* 4125 */
			3, // MIPS_SYS(sys_sigprocmask , 3)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was create_module */
			5, // MIPS_SYS(sys_init_module , 5)
			1, // MIPS_SYS(sys_delete_module, 1)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* 4130 was get_kernel_syms */
			0, // MIPS_SYS(sys_quotactl , 0)
			1, // MIPS_SYS(sys_getpgid , 1)
			1, // MIPS_SYS(sys_fchdir , 1)
			2, // MIPS_SYS(sys_bdflush , 2)
			3, // MIPS_SYS(sys_sysfs , 3) /* 4135 */
			1, // MIPS_SYS(sys_personality , 1)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* for afs_syscall */
			1, // MIPS_SYS(sys_setfsuid , 1)
			1, // MIPS_SYS(sys_setfsgid , 1)
			5, // MIPS_SYS(sys_llseek , 5) /* 4140 */
			3, // MIPS_SYS(sys_getdents , 3)
			5, // MIPS_SYS(sys_select , 5)
			2, // MIPS_SYS(sys_flock , 2)
			3, // MIPS_SYS(sys_msync , 3)
			3, // MIPS_SYS(sys_readv , 3) /* 4145 */
			3, // MIPS_SYS(sys_writev , 3)
			3, // MIPS_SYS(sys_cacheflush , 3)
			3, // MIPS_SYS(sys_cachectl , 3)
			4, // MIPS_SYS(sys_sysmips , 4)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* 4150 */
			1, // MIPS_SYS(sys_getsid , 1)
			0, // MIPS_SYS(sys_fdatasync , 0)
			1, // MIPS_SYS(sys_sysctl , 1)
			2, // MIPS_SYS(sys_mlock , 2)
			2, // MIPS_SYS(sys_munlock , 2) /* 4155 */
			1, // MIPS_SYS(sys_mlockall , 1)
			0, // MIPS_SYS(sys_munlockall , 0)
			2, // MIPS_SYS(sys_sched_setparam, 2)
			2, // MIPS_SYS(sys_sched_getparam, 2)
			3, // MIPS_SYS(sys_sched_setscheduler, 3) /* 4160 */
			1, // MIPS_SYS(sys_sched_getscheduler, 1)
			0, // MIPS_SYS(sys_sched_yield , 0)
			1, // MIPS_SYS(sys_sched_get_priority_max, 1)
			1, // MIPS_SYS(sys_sched_get_priority_min, 1)
			2, // MIPS_SYS(sys_sched_rr_get_interval, 2) /* 4165 */
			2, // MIPS_SYS(sys_nanosleep, 2)
			5, // MIPS_SYS(sys_mremap , 5)
			3, // MIPS_SYS(sys_accept , 3)
			3, // MIPS_SYS(sys_bind , 3)
			3, // MIPS_SYS(sys_connect , 3) /* 4170 */
			3, // MIPS_SYS(sys_getpeername , 3)
			3, // MIPS_SYS(sys_getsockname , 3)
			5, // MIPS_SYS(sys_getsockopt , 5)
			2, // MIPS_SYS(sys_listen , 2)
			4, // MIPS_SYS(sys_recv , 4) /* 4175 */
			6, // MIPS_SYS(sys_recvfrom , 6)
			3, // MIPS_SYS(sys_recvmsg , 3)
			4, // MIPS_SYS(sys_send , 4)
			3, // MIPS_SYS(sys_sendmsg , 3)
			6, // MIPS_SYS(sys_sendto , 6) /* 4180 */
			5, // MIPS_SYS(sys_setsockopt , 5)
			2, // MIPS_SYS(sys_shutdown , 2)
			3, // MIPS_SYS(sys_socket , 3)
			4, // MIPS_SYS(sys_socketpair , 4)
			3, // MIPS_SYS(sys_setresuid , 3) /* 4185 */
			3, // MIPS_SYS(sys_getresuid , 3)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* was sys_query_module */
			3, // MIPS_SYS(sys_poll , 3)
			3, // MIPS_SYS(sys_nfsservctl , 3)
			3, // MIPS_SYS(sys_setresgid , 3) /* 4190 */
			3, // MIPS_SYS(sys_getresgid , 3)
			5, // MIPS_SYS(sys_prctl , 5)
			0, // MIPS_SYS(sys_rt_sigreturn, 0)
			4, // MIPS_SYS(sys_rt_sigaction, 4)
			4, // MIPS_SYS(sys_rt_sigprocmask, 4) /* 4195 */
			2, // MIPS_SYS(sys_rt_sigpending, 2)
			4, // MIPS_SYS(sys_rt_sigtimedwait, 4)
			3, // MIPS_SYS(sys_rt_sigqueueinfo, 3)
			0, // MIPS_SYS(sys_rt_sigsuspend, 0)
			6, // MIPS_SYS(sys_pread64 , 6) /* 4200 */
			6, // MIPS_SYS(sys_pwrite64 , 6)
			3, // MIPS_SYS(sys_chown , 3)
			2, // MIPS_SYS(sys_getcwd , 2)
			2, // MIPS_SYS(sys_capget , 2)
			2, // MIPS_SYS(sys_capset , 2) /* 4205 */
			2, // MIPS_SYS(sys_sigaltstack , 2)
			4, // MIPS_SYS(sys_sendfile , 4)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			0, // MIPS_SYS(sys_ni_syscall , 0)
			6, // MIPS_SYS(sys_mmap2 , 6) /* 4210 */
			3, // MIPS_SYS(sys_truncate64 , 4)
			4, // MIPS_SYS(sys_ftruncate64 , 4)
			2, // MIPS_SYS(sys_stat64 , 2)
			2, // MIPS_SYS(sys_lstat64 , 2)
			2, // MIPS_SYS(sys_fstat64 , 2) /* 4215 */
			2, // MIPS_SYS(sys_pivot_root , 2)
			3, // MIPS_SYS(sys_mincore , 3)
			3, // MIPS_SYS(sys_madvise , 3)
			3, // MIPS_SYS(sys_getdents64 , 3)
			3, // MIPS_SYS(sys_fcntl64 , 3) /* 4220 */
			0, // MIPS_SYS(sys_ni_syscall , 0)
			0, // MIPS_SYS(sys_gettid , 0)
			5, // MIPS_SYS(sys_readahead , 5)
			5, // MIPS_SYS(sys_setxattr , 5)
			5, // MIPS_SYS(sys_lsetxattr , 5) /* 4225 */
			5, // MIPS_SYS(sys_fsetxattr , 5)
			4, // MIPS_SYS(sys_getxattr , 4)
			4, // MIPS_SYS(sys_lgetxattr , 4)
			4, // MIPS_SYS(sys_fgetxattr , 4)
			3, // MIPS_SYS(sys_listxattr , 3) /* 4230 */
			3, // MIPS_SYS(sys_llistxattr , 3)
			3, // MIPS_SYS(sys_flistxattr , 3)
			2, // MIPS_SYS(sys_removexattr , 2)
			2, // MIPS_SYS(sys_lremovexattr, 2)
			2, // MIPS_SYS(sys_fremovexattr, 2) /* 4235 */
			2, // MIPS_SYS(sys_tkill , 2)
			5, // MIPS_SYS(sys_sendfile64 , 5)
			6, // MIPS_SYS(sys_futex , 6)
			3, // MIPS_SYS(sys_sched_setaffinity, 3)
			3, // MIPS_SYS(sys_sched_getaffinity, 3) /* 4240 */
			2, // MIPS_SYS(sys_io_setup , 2)
			1, // MIPS_SYS(sys_io_destroy , 1)
			5, // MIPS_SYS(sys_io_getevents, 5)
			3, // MIPS_SYS(sys_io_submit , 3)
			3, // MIPS_SYS(sys_io_cancel , 3) /* 4245 */
			1, // MIPS_SYS(sys_exit_group , 1)
			3, // MIPS_SYS(sys_lookup_dcookie, 3)
			1, // MIPS_SYS(sys_epoll_create, 1)
			4, // MIPS_SYS(sys_epoll_ctl , 4)
			3, // MIPS_SYS(sys_epoll_wait , 3) /* 4250 */
			5, // MIPS_SYS(sys_remap_file_pages, 5)
			1, // MIPS_SYS(sys_set_tid_address, 1)
			0, // MIPS_SYS(sys_restart_syscall, 0)
			7, // MIPS_SYS(sys_fadvise64_64, 7)
			3, // MIPS_SYS(sys_statfs64 , 3) /* 4255 */
			2, // MIPS_SYS(sys_fstatfs64 , 2)
			3, // MIPS_SYS(sys_timer_create, 3)
			4, // MIPS_SYS(sys_timer_settime, 4)
			2, // MIPS_SYS(sys_timer_gettime, 2)
			1, // MIPS_SYS(sys_timer_getoverrun, 1) /* 4260 */
			1, // MIPS_SYS(sys_timer_delete, 1)
			2, // MIPS_SYS(sys_clock_settime, 2)
			2, // MIPS_SYS(sys_clock_gettime, 2)
			2, // MIPS_SYS(sys_clock_getres, 2)
			4, // MIPS_SYS(sys_clock_nanosleep, 4) /* 4265 */
			3, // MIPS_SYS(sys_tgkill , 3)
			2, // MIPS_SYS(sys_utimes , 2)
			4, // MIPS_SYS(sys_mbind , 4)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* sys_get_mempolicy */
			0, // MIPS_SYS(sys_ni_syscall , 0) /* 4270 sys_set_mempolicy */
			4, // MIPS_SYS(sys_mq_open , 4)
			1, // MIPS_SYS(sys_mq_unlink , 1)
			5, // MIPS_SYS(sys_mq_timedsend, 5)
			5, // MIPS_SYS(sys_mq_timedreceive, 5)
			2, // MIPS_SYS(sys_mq_notify , 2) /* 4275 */
			3, // MIPS_SYS(sys_mq_getsetattr, 3)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* sys_vserver */
			4, // MIPS_SYS(sys_waitid , 4)
			0, // MIPS_SYS(sys_ni_syscall , 0) /* available, was setaltroot */
			5, // MIPS_SYS(sys_add_key , 5)
			4, // MIPS_SYS(sys_request_key, 4)
			5, // MIPS_SYS(sys_keyctl , 5)
			1, // MIPS_SYS(sys_set_thread_area, 1)
			0, // MIPS_SYS(sys_inotify_init, 0)
			3, // MIPS_SYS(sys_inotify_add_watch, 3) /* 4285 */
			2, // MIPS_SYS(sys_inotify_rm_watch, 2)
			4, // MIPS_SYS(sys_migrate_pages, 4)
			4, // MIPS_SYS(sys_openat, 4)
			3, // MIPS_SYS(sys_mkdirat, 3)
			4, // MIPS_SYS(sys_mknodat, 4) /* 4290 */
			5, // MIPS_SYS(sys_fchownat, 5)
			3, // MIPS_SYS(sys_futimesat, 3)
			4, // MIPS_SYS(sys_fstatat64, 4)
			3, // MIPS_SYS(sys_unlinkat, 3)
			4, // MIPS_SYS(sys_renameat, 4) /* 4295 */
			5, // MIPS_SYS(sys_linkat, 5)
			3, // MIPS_SYS(sys_symlinkat, 3)
			4, // MIPS_SYS(sys_readlinkat, 4)
			3, // MIPS_SYS(sys_fchmodat, 3)
			3, // MIPS_SYS(sys_faccessat, 3) /* 4300 */
			6, // MIPS_SYS(sys_pselect6, 6)
			5, // MIPS_SYS(sys_ppoll, 5)
			1, // MIPS_SYS(sys_unshare, 1)
			6, // MIPS_SYS(sys_splice, 6)
			7, // MIPS_SYS(sys_sync_file_range, 7) /* 4305 */
			4, // MIPS_SYS(sys_tee, 4)
			4, // MIPS_SYS(sys_vmsplice, 4)
			6, // MIPS_SYS(sys_move_pages, 6)
			2, // MIPS_SYS(sys_set_robust_list, 2)
			3, // MIPS_SYS(sys_get_robust_list, 3) /* 4310 */
			4, // MIPS_SYS(sys_kexec_load, 4)
			3, // MIPS_SYS(sys_getcpu, 3)
			6, // MIPS_SYS(sys_epoll_pwait, 6)
			3, // MIPS_SYS(sys_ioprio_set, 3)
			2, // MIPS_SYS(sys_ioprio_get, 2)
			4, // MIPS_SYS(sys_utimensat, 4)
			3, // MIPS_SYS(sys_signalfd, 3)
			0, // MIPS_SYS(sys_ni_syscall, 0) /* was timerfd */
			1, // MIPS_SYS(sys_eventfd, 1)
			6, // MIPS_SYS(sys_fallocate, 6) /* 4320 */
			2, // MIPS_SYS(sys_timerfd_create, 2)
			2, // MIPS_SYS(sys_timerfd_gettime, 2)
			4, // MIPS_SYS(sys_timerfd_settime, 4)
			4, // MIPS_SYS(sys_signalfd4, 4)
			2, // MIPS_SYS(sys_eventfd2, 2) /* 4325 */
			1, // MIPS_SYS(sys_epoll_create1, 1)
			3, // MIPS_SYS(sys_dup3, 3)
			2, // MIPS_SYS(sys_pipe2, 2)
			1, // MIPS_SYS(sys_inotify_init1, 1)
			6, // MIPS_SYS(sys_preadv, 6) /* 4330 */
			6, // MIPS_SYS(sys_pwritev, 6)
			4, // MIPS_SYS(sys_rt_tgsigqueueinfo, 4)
			5, // MIPS_SYS(sys_perf_event_open, 5)
			4, // MIPS_SYS(sys_accept4, 4)
			5, // MIPS_SYS(sys_recvmmsg, 5) /* 4335 */
			2, // MIPS_SYS(sys_fanotify_init, 2)
			6, // MIPS_SYS(sys_fanotify_mark, 6)
			4, // MIPS_SYS(sys_prlimit64, 4)
			5, // MIPS_SYS(sys_name_to_handle_at, 5)
			3, // MIPS_SYS(sys_open_by_handle_at, 3) /* 4340 */
			2, // MIPS_SYS(sys_clock_adjtime, 2)
			1 // MIPS_SYS(sys_syncfs, 1)
	};

	public Integer doSyscall(List<Integer> arguments) {
		
		FileSystem fileSystem = FileSystem.getInstance();
		
		Integer ret = 0;
		Integer syscallNo = arguments.get(0);
		// write the syscall no into display
		Display display = Display.getInstance();
//		display.printString(SystemCall.mips_syscall_names[syscallNo]);
		if (sTrace.get(SystemCall.mips_syscall_names[syscallNo]) != null) {
			int count = sTrace.get(SystemCall.mips_syscall_names[syscallNo]);
			sTrace.put(SystemCall.mips_syscall_names[syscallNo], count+1);
		}
		else {
		sTrace.put(SystemCall.mips_syscall_names[syscallNo], 1);
		}
		
		switch (syscallNo) {
		case 0:break;
		case 1: //sys_exit
//			for (Map.Entry<String, Integer> entry : sTrace.entrySet()) {
//				String str = entry.getKey() + "-" + entry.getValue() + " ";
//				display.printString(str);
//			}
//			display.printString("Program Successfully terminated...");
			CPU.getInstance().video.update();
			break;
			
		case 2:break;
		case 3:
			//sys_read (fd, buf, len)
			ret = fileSystem.read(arguments.get(1), arguments.get(2), arguments.get(3));
			break;
			
		case 4:
			//sys_write (fd, buf, len)
			ret = fileSystem.write(arguments.get(1), arguments.get(2), arguments.get(3));
			break;
		
		case 5:
			//sys_open (fileName, mode, )
			fileSystem.open(arguments.get(1), arguments.get(2), arguments.get(3));
			break;
		case 6:break;
		case 7:break;
		case 8:break;
		case 9:break;
		case 10:break;
		case 11:break;
		case 12:break;
		case 13:break;
		case 14:break;
		case 15:break;
		case 16:break;
		case 17:break;
		case 18:break;
		case 19:break;
		case 20:break;
		case 21:break;
		case 22:break;
		case 23:break;
		case 24:break;
		case 25:break;
		case 26:break;
		case 27:break;
		case 28:break;
		case 29:break;
		case 30:break;
		case 31:break;
		case 32:break;
		case 33:break;
		case 34:break;
		case 35:break;
		case 36:break;
		case 37:break;
		case 38:break;
		case 39:break;
		case 40:break;
		case 41:break;
		case 42:break;
		case 43:break;
		case 44:break;
		case 45:
			//sys_brk
			ret = 1;
			break;
			
		case 46:break;
		case 47:break;
		case 48:break;
		case 49:break;
		case 50:break;
		case 51:break;
		case 52:break;
		case 53:break;
		case 54:break;
		case 55:break;
		case 56:break;
		case 57:break;
		case 58:break;
		case 59:break;
		case 60:break;
		case 61:break;
		case 62:break;
		case 63:break;
		case 64:break;
		case 65:break;
		case 66:break;
		case 67:break;
		case 68:break;
		case 69:break;
		case 70:break;
		case 71:break;
		case 72:break;
		case 73:break;
		case 74:break;
		case 75:break;
		case 76:break;
		case 77:break;
		case 78:break;
		case 79:break;
		case 80:break;
		case 81:break;
		case 82:break;
		case 83:break;
		case 84:break;
		case 85:break;
		case 86:break;
		case 87:break;
		case 88:break;
		case 89:break;
		case 90:break;
		case 91:break;
		case 92:break;
		case 93:break;
		case 94:break;
		case 95:break;
		case 96:break;
		case 97:break;
		case 98:break;
		case 99:break;
		case 100:break;
		case 101:break;
		case 102:break;
		case 103:break;
		case 104:break;
		case 105:break;
		case 106:break;
		case 107:break;
		case 108:break;
		case 109:break;
		case 110:break;
		case 111:break;
		case 112:break;
		case 113:break;
		case 114:break;
		case 115:break;
		case 116:break;
		case 117:break;
		case 118:break;
		case 119:break;
		case 120:break;
		case 121:break;
		case 122:break;
		case 123:break;
		case 124:break;
		case 125:break;
		case 126:break;
		case 127:break;
		case 128:break;
		case 129:break;
		case 130:break;
		case 131:break;
		case 132:break;
		case 133:break;
		case 134:break;
		case 135:break;
		case 136:break;
		case 137:break;
		case 138:break;
		case 139:break;
		case 140:break;
		case 141:break;
		case 142:break;
		case 143:break;
		case 144:break;
		case 145:break;
		case 146:break;
		case 147:break;
		case 148:break;
		case 149:break;
		case 150:break;
		case 151:break;
		case 152:break;
		case 153:break;
		case 154:break;
		case 155:break;
		case 156:break;
		case 157:break;
		case 158:break;
		case 159:break;
		case 160:break;
		case 161:break;
		case 162:break;
		case 163:break;
		case 164:break;
		case 165:break;
		case 166:break;
		case 167:break;
		case 168:break;
		case 169:break;
		case 170:break;
		case 171:break;
		case 172:break;
		case 173:break;
		case 174:break;
		case 175:break;
		case 176:break;
		case 177:break;
		case 178:break;
		case 179:break;
		case 180:break;
		case 181:break;
		case 182:break;
		case 183:break;
		case 184:break;
		case 185:break;
		case 186:break;
		case 187:break;
		case 188:break;
		case 189:break;
		case 190:break;
		case 191:break;
		case 192:break;
		case 193:break;
		case 194:
			//sys_rt_sigaction
			ret = 1;
			break;
		
		case 195:
			//sys_ret_sigprocmask
			ret = 1;
			break;
			
		case 196:break;
		case 197:break;
		case 198:break;
		case 199:break;
		case 200:break;
		case 201:break;
		case 202:break;
		case 203:break;
		case 204:break;
		case 205:break;
		case 206:break;
		case 207:break;
		case 208:break;
		case 209:break;
		case 210:break;
		case 211:break;
		case 212:break;
		case 213:break;
		case 214:break;
		case 215:break;
		case 216:break;
		case 217:break;
		case 218:break;
		case 219:break;
		case 220:break;
		case 221:break;
		case 222:
			//sys_gettid
			ret = 1;
			break;
			
		case 223:break;
		case 224:break;
		case 225:break;
		case 226:break;
		case 227:break;
		case 228:break;
		case 229:break;
		case 230:break;
		case 231:break;
		case 232:break;
		case 233:break;
		case 234:break;
		case 235:break;
		case 236:break;
		case 237:break;
		case 238:break;
		case 239:break;
		case 240:break;
		case 241:break;
		case 242:break;
		case 243:break;
		case 244:break;
		case 245:break;
		case 246:
			//sys_exit_group
			ret = 1;
			break;
			
		case 247:break;
		case 248:break;
		case 249:break;
		case 250:break;
		case 251:break;
		case 252:break;
		case 253:break;
		case 254:break;
		case 255:break;
		case 256:break;
		case 257:break;
		case 258:break;
		case 259:break;
		case 260:break;
		case 261:break;
		case 262:break;
		case 263:break;
		case 264:break;
		case 265:break;
		case 266:
			//sys_tgkill
			ret = 1;
			break;
			
		case 267:break;
		case 268:break;
		case 269:break;
		case 270:break;
		case 271:break;
		case 272:break;
		case 273:break;
		case 274:break;
		case 275:break;
		case 276:break;
		case 277:break;
		case 278:break;
		case 279:break;
		case 280:break;
		case 281:break;
		case 282:break;
		case 283:
			//sys_set_thread_area
			ret = 1;
			break;
			
		case 284:break;
		case 285:break;
		case 286:break;
		case 287:break;
		case 288:break;
		case 289:break;
		case 290:break;
		case 291:break;
		case 292:break;
		case 293:break;
		case 294:break;
		case 295:break;
		case 296:break;
		case 297:break;
		case 298:break;
		case 299:break;
		case 300:break;
		case 301:break;
		case 302:break;
		case 303:break;
		case 304:break;
		case 305:break;
		case 306:break;
		case 307:break;
		case 308:break;
		case 309:break;
		case 310:break;
		case 311:break;
		case 312:break;
		case 313:break;
		case 314:break;
		case 315:break;
		case 316:break;
		case 317:break;
		case 318:break;
		case 319:break;
		case 320:break;
		case 321:break;
		case 322:break;
		case 323:break;
		case 324:break;
		case 325:break;
		case 326:break;
		case 327:break;
		case 328:break;
		case 329:break;
		case 330:break;
		case 331:break;
		case 332:break;
		case 333:break;
		case 334:break;
		case 335:break;
		case 336:break;
		case 337:break;
		case 338:break;
		case 339:break;
		case 340:break;
		case 341:break;
		case 342:break;
		case 343:break;
		case 344:break;
		case 345:break;
		case 346:break;
		case 347:break;
		case 348:break;
		case 349:break;
		case 350:break;
		
		}
		
		return ret;
	}

}
